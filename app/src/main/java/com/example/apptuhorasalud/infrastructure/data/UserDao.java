package com.example.apptuhorasalud.infrastructure.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.apptuhorasalud.infrastructure.entitys.UserEntity;

@Dao
public interface UserDao {
    @Insert
    void insert(UserEntity user);

    @Update
    void update(UserEntity user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity findByEmail(String email);

    @Query("SELECT * FROM users WHERE document = :document LIMIT 1")
    UserEntity findByDocument(int document);
}