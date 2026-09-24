package com.example.apptuhorasalud.infrastructure.mappers;

import com.example.apptuhorasalud.domain.models.Medicine;
import com.example.apptuhorasalud.infrastructure.entitys.MedicineEntity;

public class MedicineMapper {
    public static MedicineEntity toEntity(Medicine model) {
        if (model == null) return null;
        MedicineEntity entity = new MedicineEntity(
                model.getId() != 0 ? Long.valueOf(model.getId()) : null,
                model.getName(),
                model.getQuantity(),
                model.getUserId(),
                model.isDeleted()
        );
        entity.setStockMinimo(model.getStockMinimo());
        return entity;
    }

    public static Medicine toDomain(MedicineEntity entity) {
        if (entity == null) return null;
        Medicine medicine = new Medicine(
                entity.getId() != null ? entity.getId().intValue() : 0,
                entity.getName(),
                entity.getQuantity(),
                entity.getUserId(),
                entity.isDeleted()
        );
        medicine.setStockMinimo(entity.getStockMinimo());
        return medicine;
    }
}