package com.medical.onepay.core.features.Tenant.infrastructure.outbound;

import com.medical.onepay.core.base.BaseRepository;
import com.medical.onepay.core.features.Tenant.domain.model.TenantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TenantJpaRepository extends BaseRepository <TenantEntity, UUID> {
    Optional<TenantEntity> findByTenantCode(String tenantCode);
    Page<TenantEntity> findByRazonSocialEmisorContainingIgnoreCase(String razonSocialEmisor, Pageable pageable);
}
