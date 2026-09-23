package com.example.apptuhorasalud.infrastructure.mappers;

import com.example.apptuhorasalud.domain.models.Medicine;
import com.example.apptuhorasalud.infrastructure.entitys.MedicineEntity;

public class MedicineMapper {
    public static MedicineEntity toEntity(Medicine model) {
        if (model == null) return null;
        return new MedicineEntity(
                model.getId() != 0 ? Long.valueOf(model.getId()) : null,
                model.getName(),
                model.getQuantity(),
                model.getUserId(),
                model.isDeleted()
        );
    }

    public static Medicine toDomain(MedicineEntity entity) {
        if (entity == null) return null;
        return new Medicine(
                entity.getId() != null ? entity.getId().intValue() : 0,
                entity.getName(),
                entity.getQuantity(),
                entity.getUserId(),
                entity.isDeleted()
        );
    }
}
