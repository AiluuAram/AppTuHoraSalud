package com.example.apptuhorasalud;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.apptuhorasalud.adapters.AlarmAdapter;
import com.example.apptuhorasalud.application.useCase.Alarm.DeleteAlarm;
import com.example.apptuhorasalud.application.useCase.Alarm.ToggleAlarm;
import com.example.apptuhorasalud.domain.interfaces.IAlarmRepository;
import com.example.apptuhorasalud.domain.models.Alarm;
import com.example.apptuhorasalud.infrastructure.data.AppDatabase;
import com.example.apptuhorasalud.infrastructure.repository.AlarmRepositoryImpl;
import com.example.apptuhorasalud.utils.AlarmScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private Button addMedicineButton;
    private Button addAlarmButton;
    private Button buttonIngresoStock;
    private RecyclerView recyclerViewAlarms;
    private TextView tvEmptyAlarms;
    private EditText etFilterMedicineName;
    private Spinner spinnerFilterStatus;
    private AlarmAdapter alarmAdapter;
    private List<Alarm> alarmList;
    private List<Alarm> allAlarms;
    private int idUsuario;

    private static final String DB_NAME = "usuarios-db";

    private static final int STATUS_ALL = 0;
    private static final int STATUS_ACTIVE = 1;
    private static final int STATUS_INACTIVE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String nombreUsuario = intent.getStringExtra("nombreUsuario");
        idUsuario = intent.getIntExtra("idUsuario", -1);

        addMedicineButton = findViewById(R.id.buttonMedicine);
        addAlarmButton = findViewById(R.id.buttonAlarm);
        buttonIngresoStock = findViewById(R.id.buttonIngresoStock);
        recyclerViewAlarms = findViewById(R.id.recyclerViewAlarms);
        tvEmptyAlarms = findViewById(R.id.tvEmptyAlarms);
        etFilterMedicineName = findViewById(R.id.etFilterMedicineName);
        spinnerFilterStatus = findViewById(R.id.spinnerFilterStatus);

        alarmList = new ArrayList<>();
        allAlarms = new ArrayList<>();
        alarmAdapter = new AlarmAdapter(alarmList, this::onAlarmToggle, this::onAlarmDelete);
        recyclerViewAlarms.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAlarms.setAdapter(alarmAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Todas", "Activas", "Inactivas"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterStatus.setAdapter(statusAdapter);

        spinnerFilterStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        etFilterMedicineName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        addMedicineButton.setOnClickListener(v -> {
            Intent intentMed = new Intent(this, ListMedicinesActivity.class);
            intentMed.putExtra("idUsuario", idUsuario);
            startActivity(intentMed);
        });

        addAlarmButton.setOnClickListener(v -> {
            Intent intentAlarm = new Intent(this, InsertAlarmActivity.class);
            intentAlarm.putExtra("idUsuario", idUsuario);
            startActivity(intentAlarm);
        });

        buttonIngresoStock.setOnClickListener(v -> {
            Intent intentIngreso = new Intent(this, ListIngresosStockActivity.class);
            intentIngreso.putExtra("idUsuario", idUsuario);
            startActivity(intentIngreso);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAlarms();
    }

    private void loadAlarms() {
        if (idUsuario == -1) return;

        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
            IAlarmRepository repo = new AlarmRepositoryImpl(db.alarmDao());

            repo.getAlarmsByUserId(idUsuario).thenAccept(alarms -> runOnUiThread(() -> {
                allAlarms.clear();
                allAlarms.addAll(alarms);
                applyFilters();
            })).exceptionally(throwable -> {
                Log.e("DB", "Error al cargar alarmas", throwable);
                return null;
            });
        }).start();
    }

    private void applyFilters() {
        String query = etFilterMedicineName.getText().toString().trim().toLowerCase(Locale.getDefault());
        int statusFilter = spinnerFilterStatus.getSelectedItemPosition();

        alarmList.clear();
        for (Alarm alarm : allAlarms) {
            boolean matchesStatus = statusFilter == STATUS_ALL
                    || (statusFilter == STATUS_ACTIVE && alarm.isActive())
                    || (statusFilter == STATUS_INACTIVE && !alarm.isActive());

            String name = alarm.getMedicineName() == null ? "" : alarm.getMedicineName().toLowerCase(Locale.getDefault());
            boolean matchesName = query.isEmpty() || name.contains(query);

            if (matchesStatus && matchesName) {
                alarmList.add(alarm);
            }
        }

        alarmAdapter.updateList(alarmList);

        if (alarmList.isEmpty()) {
            tvEmptyAlarms.setVisibility(View.VISIBLE);
            recyclerViewAlarms.setVisibility(View.GONE);
        } else {
            tvEmptyAlarms.setVisibility(View.GONE);
            recyclerViewAlarms.setVisibility(View.VISIBLE);
        }
    }

    private void onAlarmDelete(Alarm alarm) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar alarma")
                .setMessage("¿Seguro que deseas eliminar la alarma de " + alarm.getMedicineName() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> performAlarmDelete(alarm))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void performAlarmDelete(Alarm alarm) {
        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
            IAlarmRepository repo = new AlarmRepositoryImpl(db.alarmDao());
            DeleteAlarm useCase = new DeleteAlarm(repo);

            useCase.execute(alarm.getId()).thenRun(() -> {
                AlarmScheduler.cancel(getApplicationContext(), alarm.getId());
                Log.d("DB", "Alarma eliminada: " + alarm.getMedicineName());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Alarma eliminada", Toast.LENGTH_SHORT).show();
                    allAlarms.remove(alarm);
                    applyFilters();
                });
            }).exceptionally(throwable -> {
                Log.e("DB", "Error al eliminar alarma", throwable);
                runOnUiThread(() -> Toast.makeText(this,
                        "Error al eliminar la alarma",
                        Toast.LENGTH_SHORT).show());
                return null;
            });
        }).start();
    }

    private void onAlarmToggle(Alarm alarm) {
        boolean newActive = !alarm.isActive();

        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
            IAlarmRepository repo = new AlarmRepositoryImpl(db.alarmDao());
            ToggleAlarm useCase = new ToggleAlarm(repo);

            useCase.execute(alarm.getId(), newActive).thenRun(() -> {
                if (newActive) {
                    alarm.setActive(true);
                    AlarmScheduler.schedule(getApplicationContext(), alarm);
                    Log.d("DB", "Alarma activada: " + alarm.getMedicineName());
                } else {
                    alarm.setActive(false);
                    AlarmScheduler.cancel(getApplicationContext(), alarm.getId());
                    Log.d("DB", "Alarma desactivada: " + alarm.getMedicineName());
                }

                runOnUiThread(() -> {
                    Toast.makeText(this,
                            newActive ? "Alarma activada" : "Alarma desactivada",
                            Toast.LENGTH_SHORT).show();
                    applyFilters();
                });
            }).exceptionally(throwable -> {
                Log.e("DB", "Error al cambiar estado de alarma", throwable);
                runOnUiThread(() -> Toast.makeText(this,
                        "Error al cambiar el estado de la alarma",
                        Toast.LENGTH_SHORT).show());
                return null;
            });
        }).start();
    }
}
