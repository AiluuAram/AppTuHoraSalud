package com.example.apptuhorasalud;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.apptuhorasalud.domain.interfaces.IIngresoStockRepository;
import com.example.apptuhorasalud.domain.interfaces.IMedicineRepository;
import com.example.apptuhorasalud.domain.models.IngresoStock;
import com.example.apptuhorasalud.domain.models.Medicine;
import com.example.apptuhorasalud.infrastructure.data.AppDatabase;
import com.example.apptuhorasalud.infrastructure.repository.IngresoStockRepositoryImpl;
import com.example.apptuhorasalud.infrastructure.repository.MedicineRepositoryImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class InformeStockActivity extends AppCompatActivity {

    private Spinner spinnerPeriodo;
    private TextView tvKpiIngresos, tvKpiUnidades, tvKpiAlerta, tvSinDatos;
    private LinearLayout containerGrafico, containerTabla;

    private int userId;
    private int medicamentosEnAlerta = 0;                 // situación actual
    private final List<IngresoStock> todosLosIngresos = new ArrayList<>();
    private List<String> periodos = new ArrayList<>();

    private static final String DB_NAME = "usuarios-db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_informe_stock);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollInforme), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        spinnerPeriodo = findViewById(R.id.spinnerPeriodo);
        tvKpiIngresos = findViewById(R.id.tvKpiIngresos);
        tvKpiUnidades = findViewById(R.id.tvKpiUnidades);
        tvKpiAlerta = findViewById(R.id.tvKpiAlerta);
        tvSinDatos = findViewById(R.id.tvSinDatos);
        containerGrafico = findViewById(R.id.containerGrafico);
        containerTabla = findViewById(R.id.containerTabla);

        userId = getIntent().getIntExtra("idUsuario", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarDatos();
    }

    private void cargarDatos() {
        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
            IMedicineRepository medRepo = new MedicineRepositoryImpl(db.medicineDao());
            IIngresoStockRepository ingRepo = new IngresoStockRepositoryImpl(db.ingresoStockDao());

            // 1) Medicamentos en alerta (situación actual)
            medRepo.getMedicinesByUserId(userId).thenAccept(medicines -> {
                int alerta = 0;
                for (Medicine m : medicines) {
                    if (m.isStockBajo()) alerta++;
                }
                medicamentosEnAlerta = alerta;

                // 2) Ingresos (excluyendo anulados)
                ingRepo.getIngresosByUserId(userId).thenAccept(ingresos -> {
                    todosLosIngresos.clear();
                    for (IngresoStock i : ingresos) {
                        if (!i.isDeleted()) todosLosIngresos.add(i);
                    }
                    runOnUiThread(this::armarPeriodosYSpinner);
                });
            });
        }).start();
    }

    private void armarPeriodosYSpinner() {
        TreeSet<String> set = new TreeSet<>();
        for (IngresoStock i : todosLosIngresos) {
            String p = periodoDe(i.getFecha());
            if (p != null) set.add(p);
        }
        periodos = new ArrayList<>(set);
        Collections.reverse(periodos); // el más reciente primero

        tvKpiAlerta.setText(String.valueOf(medicamentosEnAlerta));

        if (periodos.isEmpty()) {
            mostrarSinDatos(true);
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, periodos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriodo.setAdapter(adapter);

        spinnerPeriodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                generarInforme(periodos.get(position));
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // "dd/MM/yyyy HH:mm" -> "MM/yyyy"
    private String periodoDe(String fecha) {
        if (fecha == null || fecha.length() < 10) return null;
        return fecha.substring(3, 10);
    }

    private void generarInforme(String periodo) {
        Map<String, int[]> mapa = new LinkedHashMap<>(); // nombre -> [cantIngresos, sumUnidades]
        int totalIngresos = 0;
        int totalUnidades = 0;

        for (IngresoStock i : todosLosIngresos) {
            if (periodo.equals(periodoDe(i.getFecha()))) {
                String nombre = i.getMedicamentoNombre();
                int[] acum = mapa.get(nombre);
                if (acum == null) acum = new int[]{0, 0};
                acum[0] += 1;
                acum[1] += i.getCantidad();
                mapa.put(nombre, acum);
                totalIngresos += 1;
                totalUnidades += i.getCantidad();
            }
        }

        tvKpiIngresos.setText(String.valueOf(totalIngresos));
        tvKpiUnidades.setText(String.valueOf(totalUnidades));
        tvKpiAlerta.setText(String.valueOf(medicamentosEnAlerta));

        if (mapa.isEmpty()) {
            mostrarSinDatos(true);
            return;
        }
        mostrarSinDatos(false);

        int maxUnidades = 1;
        for (int[] v : mapa.values()) if (v[1] > maxUnidades) maxUnidades = v[1];

        containerGrafico.removeAllViews();
        for (Map.Entry<String, int[]> e : mapa.entrySet()) {
            containerGrafico.addView(crearBarra(e.getKey(), e.getValue()[1], maxUnidades));
        }

        containerTabla.removeAllViews();
        containerTabla.addView(crearFilaTabla("Medicamento", "Ingresos", "Unid.", true));
        for (Map.Entry<String, int[]> e : mapa.entrySet()) {
            containerTabla.addView(crearFilaTabla(e.getKey(),
                    String.valueOf(e.getValue()[0]),
                    String.valueOf(e.getValue()[1]), false));
        }
        containerTabla.addView(crearFilaTabla("TOTAL",
                String.valueOf(totalIngresos), String.valueOf(totalUnidades), true));
    }

    private View crearBarra(String nombre, int valor, int max) {
        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.VERTICAL);
        fila.setPadding(0, 8, 0, 8);

        TextView tvNombre = new TextView(this);
        tvNombre.setText(nombre + "  (" + valor + ")");
        tvNombre.setTextColor(0xFF0D47A1);
        tvNombre.setTextSize(13);
        fila.addView(tvNombre);

        LinearLayout barraCont = new LinearLayout(this);
        barraCont.setOrientation(LinearLayout.HORIZONTAL);
        barraCont.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 28));

        float peso = (float) valor / (float) max;
        if (peso <= 0) peso = 0.02f;

        View barra = new View(this);
        barra.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, peso));
        barra.setBackgroundColor(0xFF278EF5);

        View resto = new View(this);
        resto.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f - peso));

        barraCont.addView(barra);
        barraCont.addView(resto);
        fila.addView(barraCont);
        return fila;
    }

    private View crearFilaTabla(String c1, String c2, String c3, boolean negrita) {
        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setPadding(0, 10, 0, 10);

        TextView t1 = columna(c1, 2f, negrita);
        TextView t2 = columna(c2, 1f, negrita);
        TextView t3 = columna(c3, 1f, negrita);
        t2.setGravity(Gravity.CENTER);
        t3.setGravity(Gravity.CENTER);

        fila.addView(t1);
        fila.addView(t2);
        fila.addView(t3);
        return fila;
    }

    private TextView columna(String texto, float peso, boolean negrita) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextColor(negrita ? 0xFF0D47A1 : 0xFF333333);
        tv.setTextSize(14);
        if (negrita) tv.setTypeface(tv.getTypeface(), android.graphics.Typeface.BOLD);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, peso));
        return tv;
    }

    private void mostrarSinDatos(boolean sinDatos) {
        tvSinDatos.setVisibility(sinDatos ? View.VISIBLE : View.GONE);
        containerGrafico.setVisibility(sinDatos ? View.GONE : View.VISIBLE);
        containerTabla.setVisibility(sinDatos ? View.GONE : View.VISIBLE);
    }
}
