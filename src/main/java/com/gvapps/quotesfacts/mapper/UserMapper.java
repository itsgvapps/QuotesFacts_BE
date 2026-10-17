package com.gvapps.quotesfacts.mapper;

import com.gvapps.quotesfacts.dto.UserDTO;
import com.gvapps.quotesfacts.entity.UserEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDTO toDtoMapped(UserEntity entity);

    UserEntity toEntityMapped(UserDTO dto);

    default UserDTO toDto(UserEntity entity) {
        UserDTO dto = toDtoMapped(entity);
        if (dto != null) {
            dto.setNotificationCounts(entity.getNotificationCounts());
        }
        return dto;
    }

    default UserEntity toEntity(UserDTO dto) {
        UserEntity entity = toEntityMapped(dto);
        if (entity != null) {
            entity.setNotificationCounts(dto.getNotificationCounts());
        }
        return entity;
    }

    // 🔁 For updating existing entity with new DTO fields (PATCH-style)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFieldsFromDto(UserDTO dto, @MappingTarget UserEntity entity);

    default void updateEntityFromDto(UserDTO dto, UserEntity entity) {
        updateEntityFieldsFromDto(dto, entity);
        if (dto != null && dto.getNotificationCounts() != null) {
            entity.setNotificationCounts(dto.getNotificationCounts());
        }
    }
}
