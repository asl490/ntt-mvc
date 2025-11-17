package com.ntt.prueba.shared;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.ntt.prueba.exception.exception.ResourceNotFoundException;

@Transactional
public abstract class BaseServiceImpl<ENTITY extends BaseEntity, CREATE_DTO, UPDATE_DTO, RESPONSE_DTO, TFilters>
        implements BaseService<CREATE_DTO, UPDATE_DTO, RESPONSE_DTO, TFilters> {

    protected final BaseJpaRepository<ENTITY> repository;
    protected final BaseMapper<ENTITY, RESPONSE_DTO, CREATE_DTO, UPDATE_DTO> mapper;
    private final GenericSpecificationBuilder<ENTITY> specificationBuilder;

    public BaseServiceImpl(BaseJpaRepository<ENTITY> repository,
            BaseMapper<ENTITY, RESPONSE_DTO, CREATE_DTO, UPDATE_DTO> mapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.specificationBuilder = new GenericSpecificationBuilder<>();
    }

    @Override
    public RESPONSE_DTO create(CREATE_DTO request) {
        ENTITY entity = mapper.toEntity(request);
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    public RESPONSE_DTO update(Long id, UPDATE_DTO request) {
        ENTITY entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        mapper.updateEntityFromDTO(request, entity);
        entity = repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    public void delete(Long id) {
        ENTITY entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        entity.setIsDeleted(true);
        repository.save(entity);
    }

    @Override
    public void restore(Long id) {
        ENTITY entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        entity.setIsDeleted(false);
        repository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public RESPONSE_DTO getById(Long id) {
        ENTITY entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RESPONSE_DTO> getOptionalById(Long id) {
        return repository.findById(id).map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RESPONSE_DTO> getAll() {
        List<ENTITY> entities = repository.findAll();
        return mapper.toDTOList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RESPONSE_DTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ENTITY> entityPage = repository.findAll(pageable);
        List<RESPONSE_DTO> content = mapper.toDTOList(entityPage.getContent());
        return new PagedResponse<>(
                content,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages(),
                entityPage.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RESPONSE_DTO> getAll(int page, int size, TFilters filters) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<ENTITY> spec = specificationBuilder.build(filters);
        Page<ENTITY> entityPage = repository.findAll(spec, pageable);
        List<RESPONSE_DTO> domainList = mapper.toDTOList(entityPage.getContent());

        return new PagedResponse<>(
                domainList,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages(),
                entityPage.isLast());
    }

}
