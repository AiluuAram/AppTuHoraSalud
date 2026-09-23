package com.example.apptuhorasalud.application.useCase.Alarm;

import com.example.apptuhorasalud.domain.interfaces.IAlarmRepository;
import com.example.apptuhorasalud.domain.models.Alarm;

import java.util.concurrent.CompletableFuture;

public class AddAlarm {

    private final IAlarmRepository repo;

    public AddAlarm(IAlarmRepository repo) {
        this.repo = repo;
    }

    public CompletableFuture<Long> execute(Alarm alarm) {
        return repo.addAlarm(alarm);
    }
}
