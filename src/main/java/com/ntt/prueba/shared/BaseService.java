package com.ntt.prueba.shared;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseService<CREATE_DTO, UPDATE_DTO, RESPONSE_DTO, TFilters> {

    RESPONSE_DTO create(CREATE_DTO request);

    RESPONSE_DTO update(UUID id, UPDATE_DTO request);

    void delete(UUID id);

    void restore(UUID id);

    RESPONSE_DTO getById(UUID id);

    Optional<RESPONSE_DTO> getOptionalById(UUID id);

    List<RESPONSE_DTO> getAll();

    PagedResponse<RESPONSE_DTO> getAll(int page, int size);

    PagedResponse<RESPONSE_DTO> getAll(int page, int size, TFilters filters);

}
