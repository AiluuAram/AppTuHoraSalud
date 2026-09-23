package com.example.apptuhorasalud.domain.interfaces;

import com.example.apptuhorasalud.domain.models.IngresoStock;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IIngresoStockRepository {
    CompletableFuture<Void> addIngreso(IngresoStock ingreso);
    CompletableFuture<Void> updateIngreso(IngresoStock ingreso);
    CompletableFuture<Void> softDeleteIngreso(IngresoStock ingreso);
    CompletableFuture<List<IngresoStock>> getIngresosByUserId(int userId);
}