package com.ntt.prueba.shared;

import java.lang.reflect.Field;

import org.springframework.data.jpa.domain.Specification;

public class GenericSpecificationBuilder<T> {

    public Specification<T> build(Object filterDto) {
        Specification<T> spec = Specification.unrestricted();

        if (filterDto == null) {
            return spec;
        }

        for (Field field : filterDto.getClass().getDeclaredFields()) {
            field.setAccessible(true); // Allow access to private fields
            try {
                Object value = field.get(filterDto);
                if (value != null && !value.toString().isEmpty()) {
                    String fieldName = field.getName();

                    if (field.getType() == String.class) {
                        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(fieldName),
                                "%" + value.toString() + "%"));
                    } else {
                        spec = spec.and(
                                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(fieldName), value));
                    }
                }
            } catch (IllegalAccessException e) {
                // Log the error or handle it appropriately
                e.printStackTrace();
            }
        }
        return spec;
    }
}
