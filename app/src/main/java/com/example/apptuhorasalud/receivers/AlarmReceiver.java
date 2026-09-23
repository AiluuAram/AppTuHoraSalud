package com.example.apptuhorasalud.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.apptuhorasalud.AlarmRingActivity;
import com.example.apptuhorasalud.R;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    public static final String CHANNEL_ID = "tu_hora_salud_alarms_v2";
    public static final String EXTRA_ALARM_ID = "alarmId";
    public static final String EXTRA_MEDICINE_NAME = "medicineName";
    public static final String EXTRA_DOSE = "dose";

    @Override
    public void onReceive(Context context, Intent intent) {
        int alarmId = intent.getIntExtra(EXTRA_ALARM_ID, 0);
        String medicineName = intent.getStringExtra(EXTRA_MEDICINE_NAME);
        String dose = intent.getStringExtra(EXTRA_DOSE);

        if (medicineName == null) medicineName = "Medicamento";
        if (dose == null) dose = "";

        Log.d(TAG, "Alarma disparada id=" + alarmId + " medicina=" + medicineName);

        ensureChannel(context);

        Intent fullScreenIntent = new Intent(context, AlarmRingActivity.class);
        fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        fullScreenIntent.putExtra(EXTRA_ALARM_ID, alarmId);
        fullScreenIntent.putExtra(EXTRA_MEDICINE_NAME, medicineName);
        fullScreenIntent.putExtra(EXTRA_DOSE, dose);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent fullScreenPI = PendingIntent.getActivity(context, alarmId, fullScreenIntent, flags);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Hora de tomar " + medicineName)
                .setContentText("Dosis: " + dose)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Dosis: " + dose))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setContentIntent(fullScreenPI)
                .setFullScreenIntent(fullScreenPI, true);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(alarmId, builder.build());

        try {
            context.startActivity(fullScreenIntent);
        } catch (Exception e) {
            Log.w(TAG, "No se pudo abrir AlarmRingActivity directamente, se usará fullScreenIntent", e);
        }
    }

    private void ensureChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Alarmas de medicamentos",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Alarmas para tomar tus medicamentos a tiempo");
                Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                if (sound == null) sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                AudioAttributes audioAttrs = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                channel.setSound(sound, audioAttrs);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{0, 800, 600, 800, 600});
                channel.setBypassDnd(true);
                channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                nm.createNotificationChannel(channel);
            }
        }
    }
}
