package com.medical.onepay.core.features.Tenant.application.useCase;

import com.medical.onepay.core.features.Tenant.domain.model.TenantEntity;
import com.medical.onepay.core.features.Tenant.domain.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantResolver {

    private final TenantRepository tenantRepository;

    public TenantEntity resolveByHost(String host) {

        if (host.equals("localhost") || host.startsWith("127.")) {
            return null; // no tenant por host
        }

        String[] parts = host.split("\\.");

        String code = parts[0];

        return tenantRepository.findByTenantCode(code)
                .orElseThrow(() ->
                        new RuntimeException("Tenant not found for host: " + host)
                );
    }
}
