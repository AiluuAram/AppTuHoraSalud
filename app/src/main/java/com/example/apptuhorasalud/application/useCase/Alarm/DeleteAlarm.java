package com.example.apptuhorasalud.application.useCase.Alarm;

import com.example.apptuhorasalud.domain.interfaces.IAlarmRepository;

import java.util.concurrent.CompletableFuture;

public class DeleteAlarm {

    private final IAlarmRepository repo;

    public DeleteAlarm(IAlarmRepository repo) {
        this.repo = repo;
    }

    public CompletableFuture<Void> execute(int alarmId) {
        return repo.deleteAlarm(alarmId);
    }
}
