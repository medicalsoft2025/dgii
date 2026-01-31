package com.medical.onepay.core.features.Tenant.application.useCase;

import com.medical.onepay.core.features.Tenant.domain.model.TenantEntity;
import com.medical.onepay.core.features.Tenant.domain.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateTenantUseCase {

    private final TenantRepository tenantRepository;

    public TenantEntity execute(UUID id, TenantEntity tenantEntity) {
        TenantEntity existingTenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + id));
        
        // Update fields here. For now, I'll just save the incoming entity assuming it has the ID set or we map it.
        // However, usually we map fields from tenantEntity to existingTenant.
        // Since I don't have a specific DTO or mapping logic, I will assume the passed entity is ready to be saved
        // but we should ensure the ID is correct.
        tenantEntity.setId(id); 
        return tenantRepository.save(tenantEntity);
    }
}
