package com.example.apptuhorasalud.application.useCase.User;

import com.example.apptuhorasalud.domain.interfaces.IUserRepository;
import com.example.apptuhorasalud.domain.models.User;

import java.util.concurrent.CompletableFuture;

public class ValidateIdentity {
    private final IUserRepository repository;

    public ValidateIdentity(IUserRepository repository) {
        this.repository = repository;
    }

    public CompletableFuture<Boolean> execute(String email, int document) {
        return repository.getByEmailUser(email)
                .thenApply(user -> user != null && user.getDocument() == document);
    }
}
