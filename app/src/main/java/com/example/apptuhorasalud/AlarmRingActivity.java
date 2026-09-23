package com.example.apptuhorasalud;

import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apptuhorasalud.receivers.AlarmReceiver;

import java.util.Calendar;
import java.util.Locale;

public class AlarmRingActivity extends AppCompatActivity {

    private static final String TAG = "AlarmRingActivity";

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private int alarmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (km != null) km.requestDismissKeyguard(this, null);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            );
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_alarm_ring);

        alarmId = getIntent().getIntExtra(AlarmReceiver.EXTRA_ALARM_ID, 0);
        String medicineName = getIntent().getStringExtra(AlarmReceiver.EXTRA_MEDICINE_NAME);
        String dose = getIntent().getStringExtra(AlarmReceiver.EXTRA_DOSE);
        if (medicineName == null) medicineName = "Medicamento";
        if (dose == null) dose = "";

        TextView tvTime = findViewById(R.id.tvAlarmTime);
        TextView tvMedicine = findViewById(R.id.tvMedicineName);
        TextView tvDose = findViewById(R.id.tvDose);
        Button btnDismiss = findViewById(R.id.btnDismiss);

        Calendar now = Calendar.getInstance();
        tvTime.setText(String.format(Locale.getDefault(), "%02d:%02d",
                now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE)));
        tvMedicine.setText(medicineName);
        tvDose.setText(dose.isEmpty() ? "" : "Dosis: " + dose);

        startAlarmSound();
        startVibration();

        btnDismiss.setOnClickListener(v -> dismissAlarm());
    }

    private void startAlarmSound() {
        try {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (uri == null) uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if (uri == null) {
                Log.w(TAG, "No se encontró URI de tono de alarma");
                return;
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, uri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build());
            } else {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            }
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Log.d(TAG, "Reproduciendo tono de alarma");
        } catch (Exception e) {
            Log.e(TAG, "Error al reproducir el tono de alarma", e);
        }
    }

    private void startVibration() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            long[] pattern = {0, 800, 600};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
            } else {
                vibrator.vibrate(pattern, 0);
            }
        }
    }

    private void dismissAlarm() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.cancel(alarmId);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            } catch (Exception ignored) {}
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (vibrator != null) vibrator.cancel();
    }

    @Override
    public void onBackPressed() {
        // Evita cerrar la alarma con el botón atrás sin pulsar Detener.
    }
}
