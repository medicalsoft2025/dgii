package com.medical.onepay.core.features.Tenant.application.useCase;

import com.medical.onepay.core.features.Tenant.domain.model.TenantEntity;
import com.medical.onepay.core.features.Tenant.domain.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateTenantUseCase {

    private final TenantRepository tenantRepository;

    public TenantEntity execute(TenantEntity tenantEntity) {
        return tenantRepository.save(tenantEntity);
    }
}
