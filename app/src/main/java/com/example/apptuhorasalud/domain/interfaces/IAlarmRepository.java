package com.example.apptuhorasalud.domain.interfaces;

import com.example.apptuhorasalud.domain.models.Alarm;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IAlarmRepository {
    CompletableFuture<Long> addAlarm(Alarm alarm);
    CompletableFuture<Void> updateAlarm(Alarm alarm);
    CompletableFuture<Void> setAlarmActive(int alarmId, boolean isActive);
    CompletableFuture<Void> deleteAlarm(int alarmId);
    CompletableFuture<List<Alarm>> getAlarmsByUserId(int userId);
}
