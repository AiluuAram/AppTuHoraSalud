package com.example.apptuhorasalud.application.useCase.User;

import com.example.apptuhorasalud.domain.interfaces.IUserRepository;

import java.util.concurrent.CompletableFuture;

public class UpdateUserPassword {
    private final IUserRepository repository;

    public UpdateUserPassword(IUserRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Void> execute(String password, String newPassword) {
        return repository.updateUserPassword(password, newPassword);
    }
}
