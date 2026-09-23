package com.example.apptuhorasalud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
    private List<Medicine> medicineList;
    private Button btnAddMedicine;
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
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
        IMedicineRepository repo = new MedicineRepositoryImpl(db.medicineDao());
        medicineAdapter = new MedicineAdapter(medicineList, repo);
        recyclerViewMedicines.setAdapter(medicineAdapter);

        // Get userId from intent
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

    // methods
    private void loadMedicines() {
        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
            IMedicineRepository repo = new MedicineRepositoryImpl(db.medicineDao());

            repo.getMedicinesByUserId(userId).thenAccept(medicines -> {
                Log.d("DB", "Medicamentos cargados: " + medicines.size());

                runOnUiThread(() -> {
                    medicineList.clear();
                    medicineList.addAll(medicines);
                    medicineAdapter.updateList(medicineList);

                    if (medicines.isEmpty()) {
                        Toast.makeText(this, "No hay medicamentos registrados", Toast.LENGTH_SHORT).show();
                    }
                });
            }).exceptionally(throwable -> {
                Log.e("DB", "Error al cargar medicamentos", throwable);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error al cargar medicamentos", Toast.LENGTH_SHORT).show();
                });
                return null;
            });
        }).start();
    }
}
