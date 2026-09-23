package com.example.apptuhorasalud.infrastructure.repository;

import com.example.apptuhorasalud.domain.interfaces.IAlarmRepository;
import com.example.apptuhorasalud.domain.models.Alarm;
import com.example.apptuhorasalud.infrastructure.data.AlarmDao;
import com.example.apptuhorasalud.infrastructure.entitys.AlarmEntity;
import com.example.apptuhorasalud.infrastructure.mappers.AlarmMapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AlarmRepositoryImpl implements IAlarmRepository {

    private final AlarmDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AlarmRepositoryImpl(AlarmDao dao) {
        this.dao = dao;
    }

    @Override
    public CompletableFuture<Long> addAlarm(Alarm alarm) {
        return CompletableFuture.supplyAsync(() -> dao.insert(AlarmMapper.toEntity(alarm)), executor);
    }

    @Override
    public CompletableFuture<Void> updateAlarm(Alarm alarm) {
        return CompletableFuture.runAsync(() -> dao.update(AlarmMapper.toEntity(alarm)), executor);
    }

    @Override
    public CompletableFuture<Void> setAlarmActive(int alarmId, boolean isActive) {
        return CompletableFuture.runAsync(() -> dao.setActive(alarmId, isActive), executor);
    }

    @Override
    public CompletableFuture<Void> deleteAlarm(int alarmId) {
        return CompletableFuture.runAsync(() -> dao.softDelete(alarmId), executor);
    }

    @Override
    public CompletableFuture<List<Alarm>> getAlarmsByUserId(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            List<AlarmEntity> entities = dao.getAlarmsByUserId(userId);
            return entities.stream()
                    .map(AlarmMapper::toDomain)
                    .collect(Collectors.toList());
        }, executor);
    }
}
