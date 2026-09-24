package com.example.apptuhorasalud.infrastructure.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.apptuhorasalud.infrastructure.entitys.AlarmEntity;
import com.example.apptuhorasalud.infrastructure.entitys.IngresoStockEntity;
import com.example.apptuhorasalud.infrastructure.entitys.MedicineEntity;
import com.example.apptuhorasalud.infrastructure.entitys.UserEntity;

@Database(entities = {UserEntity.class, MedicineEntity.class, AlarmEntity.class, IngresoStockEntity.class}, version = 7)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao usuarioDao();
    public abstract MedicineDao medicineDao();
    public abstract AlarmDao alarmDao();
    public abstract IngresoStockDao ingresoStockDao();
}
