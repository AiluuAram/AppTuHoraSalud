package com.example.apptuhorasalud.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.apptuhorasalud.domain.models.Alarm;
import com.example.apptuhorasalud.receivers.AlarmReceiver;

import java.util.Calendar;

public class AlarmScheduler {

    private static final String TAG = "AlarmScheduler";

    public static void schedule(Context context, Alarm alarm) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarm.getId());
        intent.putExtra(AlarmReceiver.EXTRA_MEDICINE_NAME, alarm.getMedicineName());
        intent.putExtra(AlarmReceiver.EXTRA_DOSE, alarm.getDose());

        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pi = PendingIntent.getBroadcast(context, alarm.getId(), intent, flags);

        long triggerAt = nextTriggerMillis(alarm.getHour(), alarm.getMinute());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (am.canScheduleExactAlarms()) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
                Log.d(TAG, "Alarma exacta programada id=" + alarm.getId() + " triggerAt=" + triggerAt);
            } else {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
                Log.w(TAG, "Sin permiso de alarmas exactas, programada inexacta id=" + alarm.getId());
            }
        } else {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
            Log.d(TAG, "Alarma exacta programada id=" + alarm.getId() + " triggerAt=" + triggerAt);
        }
    }

    public static void cancel(Context context, int alarmId) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent intent = new Intent(context, AlarmReceiver.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pi = PendingIntent.getBroadcast(context, alarmId, intent, flags);
        am.cancel(pi);
        pi.cancel();
    }

    public static void reschedule(Context context, Alarm alarm) {
        cancel(context, alarm.getId());
        schedule(context, alarm);
    }

    private static long nextTriggerMillis(int hour, int minute) {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.set(Calendar.HOUR_OF_DAY, hour);
        target.set(Calendar.MINUTE, minute);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);
        if (!target.after(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1);
        }
        return target.getTimeInMillis();
    }
}
