package com.example.apptuhorasalud.infrastructure.mappers;

import com.example.apptuhorasalud.domain.models.User;
import com.example.apptuhorasalud.infrastructure.entitys.UserEntity;

public class UserMapper {
    public static UserEntity toEntity(User model) {
        if (model == null) return null;
        return  new UserEntity(
                model.getId(),
                model.getDocument(),
                model.getName(),
                model.getLastname(),
                model.getBirthDate(),
                model.getEmail(),
                model.getPassword()
        );
    }

    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return new User(
                entity.getId(),
                entity.getDocument(),
                entity.getName(),
                entity.getLastname(),
                entity.getBirthDate(),
                entity.getEmail(),
                entity.getPassword()
        );
    }
}
