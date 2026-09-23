package com.example.apptuhorasalud.infrastructure.mappers;

import com.example.apptuhorasalud.domain.models.Alarm;
import com.example.apptuhorasalud.infrastructure.entitys.AlarmEntity;

public class AlarmMapper {
    public static AlarmEntity toEntity(Alarm model) {
        if (model == null) return null;
        return new AlarmEntity(
                model.getId() != 0 ? Long.valueOf(model.getId()) : null,
                model.getMedicineId(),
                model.getMedicineName(),
                model.getDose(),
                model.getHour(),
                model.getMinute(),
                model.getUserId(),
                model.isDeleted(),
                model.isActive()
        );
    }

    public static Alarm toDomain(AlarmEntity entity) {
        if (entity == null) return null;
        return new Alarm(
                entity.getId() != null ? entity.getId().intValue() : 0,
                entity.getMedicineId(),
                entity.getMedicineName(),
                entity.getDose(),
                entity.getHour(),
                entity.getMinute(),
                entity.getUserId(),
                entity.isDeleted(),
                entity.isActive()
        );
    }
}
