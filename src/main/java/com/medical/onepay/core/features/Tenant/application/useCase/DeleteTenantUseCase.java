package com.medical.onepay.core.features.Tenant.application.useCase;

import com.medical.onepay.core.features.Tenant.domain.model.TenantEntity;
import com.medical.onepay.core.features.Tenant.domain.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteTenantUseCase {

    private final TenantRepository tenantRepository;

    public void execute(UUID id) {
        TenantEntity tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + id));
        
        tenant.setActive(false);
        tenantRepository.save(tenant);
    }
}
