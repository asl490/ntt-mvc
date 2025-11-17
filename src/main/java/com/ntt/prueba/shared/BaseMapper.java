package com.ntt.prueba.shared;

import java.util.List;

public interface BaseMapper<ENTITY, DTO, CREATE_DTO, UPDATE_DTO> {
    DTO toDTO(ENTITY entity);

    List<DTO> toDTOList(List<ENTITY> entities);

    ENTITY toEntity(CREATE_DTO dto);

    List<ENTITY> toEntityList(List<CREATE_DTO> dto);

    void updateEntityFromDTO(UPDATE_DTO dto, ENTITY entity);
}