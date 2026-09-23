package com.example.apptuhorasalud.domain.interfaces;

import com.example.apptuhorasalud.domain.models.Medicine;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IMedicineRepository {
    CompletableFuture<Void> addMedicine(Medicine medicine);
    CompletableFuture<Void> updateMedicine(Medicine medicine);
    CompletableFuture<Void> deleteMedicine(int medicineId);
    CompletableFuture<List<Medicine>> getMedicinesByUserId(int userId);
    CompletableFuture<List<Medicine>> getMedicinesByNameAndUserId(String name, int userId);
    CompletableFuture<Void> softDeleteMedicine(Medicine medicine);
}
