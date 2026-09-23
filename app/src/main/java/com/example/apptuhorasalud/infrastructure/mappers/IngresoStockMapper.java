package com.example.apptuhorasalud.infrastructure.mappers;

import com.example.apptuhorasalud.domain.models.IngresoStock;
import com.example.apptuhorasalud.infrastructure.entitys.IngresoStockEntity;

public class IngresoStockMapper {
    public static IngresoStockEntity toEntity(IngresoStock model) {
        if (model == null) return null;
        return new IngresoStockEntity(
                model.getId() != 0 ? Long.valueOf(model.getId()) : null,
                model.getMedicamentoId(),
                model.getMedicamentoNombre(),
                model.getCantidad(),
                model.getFecha(),
                model.getUserId(),
                model.isDeleted()
        );
    }

    public static IngresoStock toDomain(IngresoStockEntity entity) {
        if (entity == null) return null;
        return new IngresoStock(
                entity.getId() != null ? entity.getId().intValue() : 0,
                entity.getMedicamentoId(),
                entity.getMedicamentoNombre(),
                entity.getCantidad(),
                entity.getFecha(),
                entity.getUserId(),
                entity.isDeleted()
        );
    }
}
