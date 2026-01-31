package com.medical.onepay.core.features.Tenant.application.useCase;

import com.medical.onepay.core.features.Tenant.domain.model.TenantEntity;
import com.medical.onepay.core.features.Tenant.domain.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetAllTenanUseCase {

    private final TenantRepository tenantRepository;

    public Page<TenantEntity> execute(Pageable pageable) {
        return tenantRepository.findAllByActiveTrue(pageable);
    }
}
