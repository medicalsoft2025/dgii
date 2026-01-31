package com.medical.onepay.core.features.Tenant.infrastructure.outbound;

import com.medical.onepay.core.features.Tenant.domain.model.TenantEntity;
import com.medical.onepay.core.features.Tenant.domain.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TenantRepositoryImpl implements TenantRepository {

    private final TenantJpaRepository jpa;

    @Override
    public TenantEntity save(TenantEntity tenant) {
        return jpa.save(tenant);
    }

    @Override
    public Optional<TenantEntity> findById(UUID id) {
        return jpa.findById(id);
    }

    @Override
    public Page<TenantEntity> findAll(Pageable pageable, String tenantName) {
        if (tenantName != null && !tenantName.isEmpty()) {
            return jpa.findByRazonSocialEmisorContainingIgnoreCase(tenantName, pageable);
        }
        return jpa.findAllByActiveTrue(pageable);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public Optional<TenantEntity> findByTenantCode(String tenantCode) {
        return jpa.findByTenantCode(tenantCode);
    }

    @Override
    public Page<TenantEntity> findAllByActiveTrue(Pageable pageable) {
        return jpa.findAllByActiveTrue(pageable);
    }
}
