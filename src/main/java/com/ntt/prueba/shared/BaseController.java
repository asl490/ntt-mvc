package com.ntt.prueba.shared;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseController<CREATE_DTO, UPDATE_DTO, RESPONSE_DTO, TFilters> {

    protected final BaseService<CREATE_DTO, UPDATE_DTO, RESPONSE_DTO, TFilters> service;

    @PostMapping
    public ResponseEntity<RESPONSE_DTO> create(@Valid @RequestBody CREATE_DTO request) {
        RESPONSE_DTO response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RESPONSE_DTO> getById(@PathVariable String id) {
        RESPONSE_DTO response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RESPONSE_DTO>> getAll() {
        List<RESPONSE_DTO> response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paged")
    public ResponseEntity<PagedResponse<RESPONSE_DTO>> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<RESPONSE_DTO> response = service.getAll(page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/filtered-paged-by-dto")
    public ResponseEntity<PagedResponse<RESPONSE_DTO>> getAllFilteredPagedByDto(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody TFilters filters) {
        PagedResponse<RESPONSE_DTO> response = service.getAll(page, size, filters);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RESPONSE_DTO> update(@PathVariable String id, @Valid @RequestBody UPDATE_DTO request) {
        RESPONSE_DTO response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}