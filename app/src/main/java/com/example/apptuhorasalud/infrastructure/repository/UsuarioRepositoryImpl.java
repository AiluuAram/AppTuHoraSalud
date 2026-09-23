package com.example.apptuhorasalud.infrastructure.repository;

import android.util.Log;
import com.example.apptuhorasalud.domain.models.User;
import com.example.apptuhorasalud.domain.interfaces.IUserRepository;
import com.example.apptuhorasalud.infrastructure.data.UserDao;
import com.example.apptuhorasalud.infrastructure.entitys.UserEntity;
import com.example.apptuhorasalud.infrastructure.mappers.UserMapper;

import org.mindrot.jbcrypt.BCrypt;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class UsuarioRepositoryImpl implements IUserRepository {

    private final UserDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UsuarioRepositoryImpl(UserDao dao) {
        this.dao = dao;
    }

    @Override
    public CompletableFuture<Void> addUser(User user) {
        return CompletableFuture.runAsync(() -> {
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);
            dao.insert(UserMapper.toEntity(user));
        }, executor);
    }

    @Override
    public CompletableFuture<User> validateUserLogin(String email, String password) {
        return CompletableFuture.supplyAsync(() -> {
            UserEntity entity = dao.findByEmail(email);
            if (entity != null && BCrypt.checkpw(password, entity.getPassword())) {
                return UserMapper.toDomain(entity);
            }
            return null;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> updateUserPassword(String email, String newPassword) {
        return CompletableFuture.runAsync(() -> {
            UserEntity entity = dao.findByEmail(email);
            if (entity != null) {
                entity.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
                dao.update(entity);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<User> getByDniUser(int document) {
        return CompletableFuture.supplyAsync(() -> {
            UserEntity entity = dao.findByDocument(document);
            return entity != null ? UserMapper.toDomain(entity) : null;
        }, executor);
    }

    @Override
    public CompletableFuture<User> getByEmailUser(String email) {
        return CompletableFuture.supplyAsync(() -> {
            UserEntity entity = dao.findByEmail(email);
            return entity != null ? UserMapper.toDomain(entity) : null;
        }, executor);
    }
}

