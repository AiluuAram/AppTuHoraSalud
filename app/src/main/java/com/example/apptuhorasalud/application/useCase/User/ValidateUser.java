package com.example.apptuhorasalud.application.useCase.User;

import com.example.apptuhorasalud.domain.models.User;
import com.example.apptuhorasalud.domain.interfaces.IUserRepository;

import java.util.concurrent.CompletableFuture;

public class ValidateUser {
    private final IUserRepository repo;
    public ValidateUser(IUserRepository repo){
        this.repo = repo;
    }

    public CompletableFuture<User> execute(String email, String password){
        return repo.validateUserLogin(email, password);
    }
}
