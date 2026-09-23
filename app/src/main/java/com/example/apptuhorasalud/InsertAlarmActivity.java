package com.example.apptuhorasalud;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.apptuhorasalud.application.useCase.Alarm.AddAlarm;
import com.example.apptuhorasalud.domain.interfaces.IAlarmRepository;
import com.example.apptuhorasalud.domain.interfaces.IMedicineRepository;
import com.example.apptuhorasalud.domain.models.Alarm;
import com.example.apptuhorasalud.domain.models.Medicine;
import com.example.apptuhorasalud.infrastructure.data.AppDatabase;
import com.example.apptuhorasalud.infrastructure.repository.AlarmRepositoryImpl;
import com.example.apptuhorasalud.infrastructure.repository.MedicineRepositoryImpl;
import com.example.apptuhorasalud.utils.AlarmScheduler;
import com.example.apptuhorasalud.utils.FormUtils;

import java.util.ArrayList;
import java.util.List;

public class InsertAlarmActivity extends AppCompatActivity {

    private Spinner spinnerMedicine;
    private EditText inputDose;
    private TimePicker timePickerAlarm;
    private Button btnSaveAlarm;

    private List<Medicine> userMedicines = new ArrayList<>();
    private int userId;

    private static final String DB_NAME = "usuarios-db";
    private static final int REQ_NOTIFICATIONS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insert_alarm);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollInsertAlarm), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerMedicine = findViewById(R.id.spinnerMedicine);
        inputDose = findViewById(R.id.inputDose);
        timePickerAlarm = findViewById(R.id.timePickerAlarm);
        timePickerAlarm.setIs24HourView(true);
        btnSaveAlarm = findViewById(R.id.btnSaveAlarm);

        userId = getIntent().getIntExtra("idUsuario", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        requestNotificationPermissionIfNeeded();
        loadMedicines();

        btnSaveAlarm.setOnClickListener(v -> onSaveAlarmClick());
    }

    private void loadMedicines() {
        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
            IMedicineRepository repo = new MedicineRepositoryImpl(db.medicineDao());

            repo.getMedicinesByUserId(userId).thenAccept(medicines -> runOnUiThread(() -> {
                userMedicines.clear();
                userMedicines.addAll(medicines);

                List<String> names = new ArrayList<>();
                for (Medicine m : medicines) names.add(m.getName());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMedicine.setAdapter(adapter);

                if (medicines.isEmpty()) {
                    Toast.makeText(this, "Primero registra un medicamento", Toast.LENGTH_LONG).show();
                    btnSaveAlarm.setEnabled(false);
                }
            }));
        }).start();
    }

    private void onSaveAlarmClick() {
        if (userMedicines.isEmpty()) {
            Toast.makeText(this, "No hay medicamentos disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        int position = spinnerMedicine.getSelectedItemPosition();
        if (position < 0 || position >= userMedicines.size()) {
            Toast.makeText(this, "Seleccione un medicamento", Toast.LENGTH_SHORT).show();
            return;
        }

        String dose = inputDose.getText().toString().trim();
        if (TextUtils.isEmpty(dose)) {
            FormUtils.showError(this, "Ingrese la dosis", inputDose);
            return;
        }

        int hour = timePickerAlarm.getHour();
        int minute = timePickerAlarm.getMinute();
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            Toast.makeText(this, "Hora inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        Medicine selected = userMedicines.get(position);
        Alarm alarm = new Alarm(0, selected.getId(), selected.getName(), dose, hour, minute, userId, false);

        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
            IAlarmRepository repo = new AlarmRepositoryImpl(db.alarmDao());
            AddAlarm useCase = new AddAlarm(repo);

            useCase.execute(alarm).thenAccept(generatedId -> {
                alarm.setId(generatedId.intValue());
                AlarmScheduler.schedule(getApplicationContext(), alarm);
                Log.d("DB", "Alarma guardada: " + alarm.getMedicineName() + " a las " + hour + ":" + minute);

                runOnUiThread(() -> {
                    FormUtils.showSuccess(this, "Alarma guardada correctamente");
                    finish();
                });
            }).exceptionally(throwable -> {
                Log.e("DB", "Error al guardar alarma", throwable);
                runOnUiThread(() -> Toast.makeText(this, "Error al guardar la alarma", Toast.LENGTH_SHORT).show());
                return null;
            });
        }).start();
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQ_NOTIFICATIONS);
            }
        }
    }
}
