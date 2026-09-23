package com.example.apptuhorasalud.domain.interfaces;

import com.example.apptuhorasalud.domain.models.User;

import java.util.concurrent.CompletableFuture;

public interface IUserRepository {
    CompletableFuture<Void> addUser(User user);
    CompletableFuture<User> validateUserLogin(String email, String password);
    CompletableFuture<Void> updateUserPassword(String email, String newPassword);
    CompletableFuture<User> getByDniUser(int document);
    CompletableFuture<User> getByEmailUser(String email);


}
