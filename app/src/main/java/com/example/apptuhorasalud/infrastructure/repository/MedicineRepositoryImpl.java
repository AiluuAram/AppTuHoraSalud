package com.example.apptuhorasalud.infrastructure.repository;

import com.example.apptuhorasalud.domain.models.Medicine;
import com.example.apptuhorasalud.domain.interfaces.IMedicineRepository;
import com.example.apptuhorasalud.infrastructure.data.MedicineDao;
import com.example.apptuhorasalud.infrastructure.entitys.MedicineEntity;
import com.example.apptuhorasalud.infrastructure.mappers.MedicineMapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MedicineRepositoryImpl implements IMedicineRepository {

    private final MedicineDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public MedicineRepositoryImpl(MedicineDao dao) {
        this.dao = dao;
    }

    @Override
    public CompletableFuture<Void> addMedicine(Medicine medicine) {
        return CompletableFuture.runAsync(() -> {
            dao.insert(MedicineMapper.toEntity(medicine));
        }, executor);
    }

    @Override
    public CompletableFuture<Void> updateMedicine(Medicine medicine) {
        return CompletableFuture.runAsync(() -> {
            dao.update(MedicineMapper.toEntity(medicine));
        }, executor);
    }

    @Override
    public CompletableFuture<Void> deleteMedicine(int medicineId) {
        return CompletableFuture.runAsync(() -> {
            MedicineEntity entity = new MedicineEntity();
            entity.setId(Long.valueOf(medicineId));
            dao.delete(entity);
        }, executor);
    }

    public CompletableFuture<Void> softDeleteMedicine(Medicine medicine) {
        medicine.setDeleted(true);
        return this.updateMedicine(medicine);
    }

    @Override
    public CompletableFuture<List<Medicine>> getMedicinesByUserId(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            List<MedicineEntity> entities = dao.getMedicinesByUserId(userId);
            return entities.stream()
                    .map(MedicineMapper::toDomain)
                    .collect(Collectors.toList());
        }, executor);
    }

    @Override
    public CompletableFuture<List<Medicine>> getMedicinesByNameAndUserId(String name, int userId) {
        return CompletableFuture.supplyAsync(() -> {
            List<MedicineEntity> entities = dao.getMedicinesByNameAndUserId(name, userId);
            return entities.stream()
                    .map(MedicineMapper::toDomain)
                    .collect(Collectors.toList());
        }, executor);
    }
}
