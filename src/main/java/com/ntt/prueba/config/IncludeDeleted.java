package com.ntt.prueba.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para indicar que un método de caso de uso debe incluir
 * las entidades marcadas como borradas (is_deleted = true) en su consulta.
 * Desactiva temporalmente el filtro global 'deletedFilter' de Hibernate.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IncludeDeleted {
}
