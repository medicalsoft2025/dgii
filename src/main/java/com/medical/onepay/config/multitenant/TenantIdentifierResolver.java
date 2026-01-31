package com.medical.onepay.config.multitenant;

import com.medical.onepay.shared.tenant.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<UUID> {

    // Un UUID por defecto que no debería coincidir con ningún tenant real
    private static final UUID DEFAULT_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Override
    public UUID resolveCurrentTenantIdentifier() {
        if (TenantContext.hasTenant()) {
            return TenantContext.getTenantId();
        }
        // Si no hay un tenant en el contexto, devolvemos un ID por defecto.
        // Esto es crucial para que Hibernate pueda inicializarse y para queries que no son tenant-specific.
        return DEFAULT_TENANT_ID;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
