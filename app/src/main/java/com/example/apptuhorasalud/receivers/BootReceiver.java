package com.example.apptuhorasalud.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.apptuhorasalud.domain.models.Alarm;
import com.example.apptuhorasalud.infrastructure.data.AlarmDao;
import com.example.apptuhorasalud.infrastructure.data.AppDatabase;
import com.example.apptuhorasalud.infrastructure.entitys.AlarmEntity;
import com.example.apptuhorasalud.infrastructure.mappers.AlarmMapper;
import com.example.apptuhorasalud.utils.AlarmScheduler;
import com.example.apptuhorasalud.utils.DatabaseHelper;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";
    private static final String ACTION_QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        if (action == null) return;

        if (!Intent.ACTION_BOOT_COMPLETED.equals(action)
                && !Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action)
                && !Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)
                && !Intent.ACTION_TIME_CHANGED.equals(action)
                && !Intent.ACTION_TIMEZONE_CHANGED.equals(action)
                && !ACTION_QUICKBOOT_POWERON.equals(action)) {
            return;
        }

        final PendingResult pendingResult = goAsync();
        final Context appContext = context.getApplicationContext();

        new Thread(() -> {
            try {
                AppDatabase db = DatabaseHelper.getInstance(appContext);
                AlarmDao dao = db.alarmDao();
                List<AlarmEntity> entities = dao.getAllActive();
                Log.d(TAG, "Reprogramando " + entities.size() + " alarmas tras evento " + action);
                for (AlarmEntity entity : entities) {
                    Alarm alarm = AlarmMapper.toDomain(entity);
                    if (alarm == null) continue;
                    try {
                        AlarmScheduler.schedule(appContext, alarm);
                    } catch (Exception e) {
                        Log.e(TAG, "Error programando alarma id=" + alarm.getId(), e);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al reprogramar alarmas", e);
            } finally {
                pendingResult.finish();
            }
        }).start();
    }
}
