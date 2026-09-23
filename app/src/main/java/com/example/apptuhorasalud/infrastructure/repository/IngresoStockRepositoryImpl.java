package com.example.apptuhorasalud.infrastructure.repository;

import com.example.apptuhorasalud.domain.models.IngresoStock;
import com.example.apptuhorasalud.domain.interfaces.IIngresoStockRepository;
import com.example.apptuhorasalud.infrastructure.data.IngresoStockDao;
import com.example.apptuhorasalud.infrastructure.entitys.IngresoStockEntity;
import com.example.apptuhorasalud.infrastructure.mappers.IngresoStockMapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class IngresoStockRepositoryImpl implements IIngresoStockRepository {

    private final IngresoStockDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public IngresoStockRepositoryImpl(IngresoStockDao dao) {
        this.dao = dao;
    }

    @Override
    public CompletableFuture<Void> addIngreso(IngresoStock ingreso) {
        return CompletableFuture.runAsync(() -> {
            dao.insert(IngresoStockMapper.toEntity(ingreso));
        }, executor);
    }

    @Override
    public CompletableFuture<Void> updateIngreso(IngresoStock ingreso) {
        return CompletableFuture.runAsync(() -> {
            dao.update(IngresoStockMapper.toEntity(ingreso));
        }, executor);
    }

    @Override
    public CompletableFuture<Void> softDeleteIngreso(IngresoStock ingreso) {
        ingreso.setDeleted(true);
        return this.updateIngreso(ingreso);
    }

    @Override
    public CompletableFuture<List<IngresoStock>> getIngresosByUserId(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            List<IngresoStockEntity> entities = dao.getIngresosByUserId(userId);
            return entities.stream()
                    .map(IngresoStockMapper::toDomain)
                    .collect(Collectors.toList());
        }, executor);
    }
}