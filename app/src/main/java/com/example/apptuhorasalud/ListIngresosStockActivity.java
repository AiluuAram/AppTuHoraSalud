package com.example.apptuhorasalud;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.apptuhorasalud.adapters.IngresoStockAdapter;
import com.example.apptuhorasalud.domain.interfaces.IIngresoStockRepository;
import com.example.apptuhorasalud.domain.interfaces.IMedicineRepository;
import com.example.apptuhorasalud.domain.models.IngresoStock;
import com.example.apptuhorasalud.domain.models.Medicine;
import com.example.apptuhorasalud.infrastructure.data.AppDatabase;
import com.example.apptuhorasalud.infrastructure.repository.IngresoStockRepositoryImpl;
import com.example.apptuhorasalud.infrastructure.repository.MedicineRepositoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListIngresosStockActivity extends AppCompatActivity {

    private RecyclerView recyclerViewIngresos;
    private IngresoStockAdapter ingresoAdapter;
    private EditText inputBuscarIngreso;
    private TextView tvEmptyIngresos;
    private Button btnNuevoIngreso;

    private final List<IngresoStock> fullList = new ArrayList<>();
    private int userId;

    private static final String DB_NAME = "usuarios-db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_ingresos_stock);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollListIngresos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewIngresos = findViewById(R.id.recyclerViewIngresos);
        inputBuscarIngreso = findViewById(R.id.inputBuscarIngreso);
        tvEmptyIngresos = findViewById(R.id.tvEmptyIngresos);
        btnNuevoIngreso = findViewById(R.id.btnNuevoIngreso);

        recyclerViewIngresos.setLayoutManager(new LinearLayoutManager(this));
        ingresoAdapter = new IngresoStockAdapter(new ArrayList<>(), new IngresoStockAdapter.OnIngresoActionListener() {
            @Override
            public void onAnular(IngresoStock ingreso) {
                confirmarAnular(ingreso);
            }

            @Override
            public void onEditar(IngresoStock ingreso) {
                abrirEditar(ingreso);
            }
        });
        recyclerViewIngresos.setAdapter(ingresoAdapter);

        userId = getIntent().getIntExtra("idUsuario", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnNuevoIngreso.setOnClickListener(v -> {
            Intent intent = new Intent(this, InsertIngresoStockActivity.class);
            intent.putExtra("idUsuario", userId);
            startActivity(intent);
        });

        inputBuscarIngreso.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIngresos();
    }

    private void loadIngresos() {
        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
            IIngresoStockRepository repo = new IngresoStockRepositoryImpl(db.ingresoStockDao());

            repo.getIngresosByUserId(userId).thenAccept(ingresos -> {
                Log.d("DB", "Ingresos cargados: " + ingresos.size());
                runOnUiThread(() -> {
                    fullList.clear();
                    fullList.addAll(ingresos);
                    applyFilter();
                });
            }).exceptionally(throwable -> {
                Log.e("DB", "Error al cargar ingresos", throwable);
                runOnUiThread(() ->
                        Toast.makeText(this, "Error al cargar ingresos", Toast.LENGTH_SHORT).show());
                return null;
            });
        }).start();
    }

    private void applyFilter() {
        String texto = inputBuscarIngreso.getText().toString().trim().toLowerCase(Locale.getDefault());

        List<IngresoStock> filtrada = new ArrayList<>();
        for (IngresoStock ingreso : fullList) {
            String nombre = ingreso.getMedicamentoNombre() == null ? "" : ingreso.getMedicamentoNombre().toLowerCase(Locale.getDefault());
            String fecha = ingreso.getFecha() == null ? "" : ingreso.getFecha().toLowerCase(Locale.getDefault());
            if (texto.isEmpty() || nombre.contains(texto) || fecha.contains(texto)) {
                filtrada.add(ingreso);
            }
        }

        ingresoAdapter.updateList(filtrada);

        if (filtrada.isEmpty()) {
            tvEmptyIngresos.setVisibility(android.view.View.VISIBLE);
            recyclerViewIngresos.setVisibility(android.view.View.GONE);
        } else {
            tvEmptyIngresos.setVisibility(android.view.View.GONE);
            recyclerViewIngresos.setVisibility(android.view.View.VISIBLE);
        }
    }

    private void abrirEditar(IngresoStock ingreso) {
        Intent intent = new Intent(this, UpdateIngresoStockActivity.class);
        intent.putExtra("ingresoId", ingreso.getId());
        intent.putExtra("medicamentoId", ingreso.getMedicamentoId());
        intent.putExtra("medicamentoNombre", ingreso.getMedicamentoNombre());
        intent.putExtra("cantidad", ingreso.getCantidad());
        intent.putExtra("fecha", ingreso.getFecha());
        intent.putExtra("idUsuario", userId);
        startActivity(intent);
    }

    private void confirmarAnular(IngresoStock ingreso) {
        new AlertDialog.Builder(this)
                .setTitle("Anular ingreso")
                .setMessage("¿Seguro que querés anular este ingreso de " + ingreso.getCantidad()
                        + " unidad(es) de " + ingreso.getMedicamentoNombre() + "? Se va a restar del stock.")
                .setPositiveButton("Anular", (dialog, which) -> anularIngreso(ingreso))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void anularIngreso(IngresoStock ingreso) {
        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
            IIngresoStockRepository ingresoRepo = new IngresoStockRepositoryImpl(db.ingresoStockDao());
            IMedicineRepository medicineRepo = new MedicineRepositoryImpl(db.medicineDao());

            ingresoRepo.softDeleteIngreso(ingreso).join();

            List<Medicine> medicines = medicineRepo.getMedicinesByUserId(userId).join();
            for (Medicine m : medicines) {
                if (m.getId() == ingreso.getMedicamentoId()) {
                    int nuevoStock = m.getQuantity() - ingreso.getCantidad();
                    if (nuevoStock < 0) nuevoStock = 0;
                    m.setQuantity(nuevoStock);
                    medicineRepo.updateMedicine(m).join();
                    break;
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Ingreso anulado", Toast.LENGTH_SHORT).show();
                loadIngresos();
            });
        }).start();
    }
}