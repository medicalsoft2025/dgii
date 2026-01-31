package com.medical.onepay.core.features.Tenant.domain.repository;

import com.medical.onepay.core.features.Tenant.domain.model.TenantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository {
    TenantEntity save(TenantEntity tenant);
    Optional<TenantEntity> findById(UUID id);
    Page<TenantEntity> findAll(Pageable pageable, String tenantName);
    void deleteById(UUID id);
    Optional<TenantEntity> findByTenantCode(String tenantCode);
    Page<TenantEntity> findAllByActiveTrue(Pageable pageable);
}
