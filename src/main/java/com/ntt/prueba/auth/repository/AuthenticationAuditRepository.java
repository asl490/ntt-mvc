package com.ntt.prueba.auth.repository;

import org.springframework.stereotype.Repository;

import com.ntt.prueba.auth.entity.AuthenticationAudit;
import com.ntt.prueba.shared.BaseJpaRepository;

@Repository
public interface AuthenticationAuditRepository extends BaseJpaRepository<AuthenticationAudit> {

}
