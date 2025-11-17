package com.ntt.prueba.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;

@Aspect
@Component
public class HibernateFilterAspect {

    private final EntityManager entityManager;

    public HibernateFilterAspect(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Around("execution(* com.ntt.prueba..*ServiceImpl.*(..)) || execution(* com.ntt.prueba..*Service.*(..))")
    public Object manageDeletedFilter(ProceedingJoinPoint joinPoint) throws Throwable {
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("deletedFilter");

        try {
            return joinPoint.proceed();
        } finally {
            // No es necesario desactivar el filtro aquí, se gestionará por transacción.
        }
    }

    @Around("@annotation(includeDeleted)")
    public Object manageIncludeDeletedAnnotation(ProceedingJoinPoint joinPoint, IncludeDeleted includeDeleted)
            throws Throwable {
        Session session = entityManager.unwrap(Session.class);

        if (session.getEnabledFilter("deletedFilter") != null) {
            session.disableFilter("deletedFilter");
        }

        try {
            return joinPoint.proceed();
        } finally {
            session.enableFilter("deletedFilter");
        }
    }
}
