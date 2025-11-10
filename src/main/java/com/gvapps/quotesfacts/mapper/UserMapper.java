package com.gvapps.quotesfacts.mapper;

import com.gvapps.quotesfacts.dto.UserDTO;
import com.gvapps.quotesfacts.entity.UserEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDTO toDto(UserEntity entity);

    UserEntity toEntity(UserDTO dto);

    // 🔁 For updating existing entity with new DTO fields (PATCH-style)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserDTO dto, @MappingTarget UserEntity entity);
}
