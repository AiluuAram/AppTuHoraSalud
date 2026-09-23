package com.example.apptuhorasalud.infrastructure.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Query;

import com.example.apptuhorasalud.infrastructure.entitys.IngresoStockEntity;

import java.util.List;

@Dao
public interface IngresoStockDao {
    @Insert
    void insert(IngresoStockEntity ingresoStockEntity);
    @Update
    void update(IngresoStockEntity ingresoStockEntity);

    @Query("SELECT * FROM ingresos_stock WHERE userId = :userId AND isDeleted = 0 ORDER BY id DESC")
    List<IngresoStockEntity> getIngresosByUserId(int userId);

    @Query("SELECT * FROM ingresos_stock WHERE id = :id")
    IngresoStockEntity getById(int id);
}
