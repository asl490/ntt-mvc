package com.ntt.prueba.shared;

import java.util.List;
import java.util.Optional;

public interface BaseService<CREATE_DTO, UPDATE_DTO, RESPONSE_DTO, TFilters> {

    RESPONSE_DTO create(CREATE_DTO request);

    RESPONSE_DTO update(Long id, UPDATE_DTO request);

    void delete(Long id);

    void restore(Long id);

    RESPONSE_DTO getById(Long id);

    Optional<RESPONSE_DTO> getOptionalById(Long id);

    List<RESPONSE_DTO> getAll();

    PagedResponse<RESPONSE_DTO> getAll(int page, int size);

    PagedResponse<RESPONSE_DTO> getAll(int page, int size, TFilters filters);

}
