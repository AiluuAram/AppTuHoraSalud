package com.example.apptuhorasalud.infrastructure.data;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.apptuhorasalud.infrastructure.entitys.MedicineEntity;

import java.util.List;

@Dao
public interface MedicineDao {
    @Insert
    void insert(MedicineEntity medicineEntity);
    @Update
    void update(MedicineEntity medicineEntity);
    @Delete
    void delete(MedicineEntity medicineEntity);

    @Query("SELECT * FROM medicines where userId = :userId and isDeleted = 0")
    List<MedicineEntity> getMedicinesByUserId(int userId);
    @Query("SELECT * FROM medicines where name = :name and userId = :userId")
    List<MedicineEntity> getMedicinesByNameAndUserId(String name, int userId);
}
