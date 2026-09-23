package com.example.apptuhorasalud;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.apptuhorasalud.application.useCase.Alarm.UpdateAlarm;
import com.example.apptuhorasalud.domain.interfaces.IAlarmRepository;
import com.example.apptuhorasalud.domain.models.Alarm;
import com.example.apptuhorasalud.infrastructure.data.AppDatabase;
import com.example.apptuhorasalud.infrastructure.repository.AlarmRepositoryImpl;
import com.example.apptuhorasalud.utils.AlarmScheduler;
import com.example.apptuhorasalud.utils.FormUtils;

public class UpdateAlarmActivity extends AppCompatActivity {

    private TextView tvMedicineName;
    private EditText inputDose;
    private TimePicker timePickerAlarm;
    private Button btnUpdateAlarm;

    private int alarmId;
    private int medicineId;
    private String medicineName;
    private int userId;
    private boolean isActive;

    private static final String DB_NAME = "usuarios-db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_alarm);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollUpdateAlarm), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvMedicineName = findViewById(R.id.tvMedicineName);
        inputDose = findViewById(R.id.inputDose);
        timePickerAlarm = findViewById(R.id.timePickerAlarm);
        timePickerAlarm.setIs24HourView(true);
        btnUpdateAlarm = findViewById(R.id.btnUpdateAlarm);

        alarmId = getIntent().getIntExtra("alarmId", -1);
        medicineId = getIntent().getIntExtra("medicineId", -1);
        medicineName = getIntent().getStringExtra("medicineName");
        String currentDose = getIntent().getStringExtra("dose");
        int currentHour = getIntent().getIntExtra("hour", 0);
        int currentMinute = getIntent().getIntExtra("minute", 0);
        userId = getIntent().getIntExtra("idUsuario", -1);
        isActive = getIntent().getBooleanExtra("isActive", true);

        if (alarmId == -1 || userId == -1) {
            Toast.makeText(this, "Error: Datos incompletos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvMedicineName.setText(medicineName != null ? medicineName : "");
        inputDose.setText(currentDose != null ? currentDose : "");
        timePickerAlarm.setHour(currentHour);
        timePickerAlarm.setMinute(currentMinute);

        btnUpdateAlarm.setOnClickListener(v -> onUpdateAlarmClick());
    }

    private void onUpdateAlarmClick() {
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

        Alarm alarm = new Alarm(alarmId, medicineId, medicineName, dose, hour, minute, userId, false, isActive);

        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
            IAlarmRepository repo = new AlarmRepositoryImpl(db.alarmDao());
            UpdateAlarm useCase = new UpdateAlarm(repo);

            useCase.execute(alarm).thenRun(() -> {
                if (isActive) {
                    AlarmScheduler.reschedule(getApplicationContext(), alarm);
                } else {
                    AlarmScheduler.cancel(getApplicationContext(), alarm.getId());
                }
                Log.d("DB", "Alarma reprogramada: " + alarm.getMedicineName() + " a las " + hour + ":" + minute);

                runOnUiThread(() -> {
                    FormUtils.showSuccess(this, "Alarma reprogramada correctamente");
                    finish();
                });
            }).exceptionally(throwable -> {
                Log.e("DB", "Error al reprogramar alarma", throwable);
                runOnUiThread(() -> Toast.makeText(this, "Error al reprogramar la alarma", Toast.LENGTH_SHORT).show());
                return null;
            });
        }).start();
    }
}
