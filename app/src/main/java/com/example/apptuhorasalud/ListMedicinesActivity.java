package com.example.apptuhorasalud;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import com.example.apptuhorasalud.adapters.MedicineAdapter;
import com.example.apptuhorasalud.domain.models.Medicine;
import com.example.apptuhorasalud.domain.interfaces.IMedicineRepository;
import com.example.apptuhorasalud.infrastructure.data.AppDatabase;
import com.example.apptuhorasalud.infrastructure.repository.MedicineRepositoryImpl;
import java.util.ArrayList;
import java.util.List;

public class ListMedicinesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMedicines;
    private MedicineAdapter medicineAdapter;
    private List<Medicine> medicineList;        // lista que se muestra (ya filtrada)
    private List<Medicine> fullMedicineList;    // lista completa sin filtrar
    private Button btnAddMedicine;
    private EditText inputBuscarMedicine;
    private TextView tvEmptyMedicines;
    private Button btnFiltroTodos, btnFiltroAlerta, btnFiltroNormal;
    private String filtroEstado = "TODOS";      // TODOS | ALERTA | NORMAL
    private int userId;

    private static final String DB_NAME = "usuarios-db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_medicines);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollListMedicines), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewMedicines = findViewById(R.id.recyclerViewMedicines);
        recyclerViewMedicines.setLayoutManager(new LinearLayoutManager(this));

        medicineList = new ArrayList<>();
        fullMedicineList = new ArrayList<>();
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
        IMedicineRepository repo = new MedicineRepositoryImpl(db.medicineDao());
        medicineAdapter = new MedicineAdapter(medicineList, repo);
        recyclerViewMedicines.setAdapter(medicineAdapter);

        // Vistas nuevas del listado (buscador, filtros y mensaje vacío)
        inputBuscarMedicine = findViewById(R.id.inputBuscarMedicine);
        tvEmptyMedicines = findViewById(R.id.tvEmptyMedicines);
        btnFiltroTodos = findViewById(R.id.btnFiltroTodos);
        btnFiltroAlerta = findViewById(R.id.btnFiltroAlerta);
        btnFiltroNormal = findViewById(R.id.btnFiltroNormal);

        // Buscador por nombre: cada vez que el usuario escribe, filtra
        inputBuscarMedicine.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { aplicarFiltros(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Botones de filtro por estado
        btnFiltroTodos.setOnClickListener(v -> { filtroEstado = "TODOS"; aplicarFiltros(); });
        btnFiltroAlerta.setOnClickListener(v -> { filtroEstado = "ALERTA"; aplicarFiltros(); });
        btnFiltroNormal.setOnClickListener(v -> { filtroEstado = "NORMAL"; aplicarFiltros(); });

        userId = getIntent().getIntExtra("idUsuario", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadMedicines();

        btnAddMedicine = findViewById(R.id.btnAddMedicine);
        btnAddMedicine.setOnClickListener(v -> {
            Intent intent = new Intent(this, InsertMedicineActivity.class);
            intent.putExtra("idUsuario", userId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMedicines();
    }

    // Carga todos los medicamentos y guarda la lista completa
    private void loadMedicines() {
        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
            IMedicineRepository repo = new MedicineRepositoryImpl(db.medicineDao());

            repo.getMedicinesByUserId(userId).thenAccept(medicines -> {
                runOnUiThread(() -> {
                    fullMedicineList.clear();
                    fullMedicineList.addAll(medicines);
                    aplicarFiltros();
                });
            }).exceptionally(throwable -> {
                Log.e("DB", "Error al cargar medicamentos", throwable);
                runOnUiThread(() ->
                        Toast.makeText(this, "Error al cargar medicamentos", Toast.LENGTH_SHORT).show());
                return null;
            });
        }).start();
    }

    // Aplica el buscador por nombre + el filtro por estado sobre la lista completa
    private void aplicarFiltros() {
        String texto = inputBuscarMedicine.getText().toString().toLowerCase().trim();
        List<Medicine> filtrados = new ArrayList<>();

        for (Medicine m : fullMedicineList) {
            boolean coincideNombre = m.getName().toLowerCase().contains(texto);

            boolean coincideEstado;
            if (filtroEstado.equals("ALERTA")) {
                coincideEstado = m.isStockBajo();       // stock actual <= mínimo
            } else if (filtroEstado.equals("NORMAL")) {
                coincideEstado = !m.isStockBajo();
            } else {
                coincideEstado = true;                  // TODOS
            }

            if (coincideNombre && coincideEstado) {
                filtrados.add(m);
            }
        }

        medicineList.clear();
        medicineList.addAll(filtrados);
        medicineAdapter.updateList(medicineList);

        // Mensaje "Sin resultados"
        tvEmptyMedicines.setVisibility(filtrados.isEmpty() ? View.VISIBLE : View.GONE);
    }
}