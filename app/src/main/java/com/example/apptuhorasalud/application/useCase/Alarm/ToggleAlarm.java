package com.example.apptuhorasalud.application.useCase.Alarm;

import com.example.apptuhorasalud.domain.interfaces.IAlarmRepository;

import java.util.concurrent.CompletableFuture;

public class ToggleAlarm {

    private final IAlarmRepository repo;

    public ToggleAlarm(IAlarmRepository repo) {
        this.repo = repo;
    }

    public CompletableFuture<Void> execute(int alarmId, boolean isActive) {
        return repo.setAlarmActive(alarmId, isActive);
    }
}
