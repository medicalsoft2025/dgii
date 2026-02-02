package com.medical.onepay.core.features.auth.application.ports.out;

import com.medical.onepay.core.features.auth.infrastructure.dto.DgiiTokenResponse;
import java.util.UUID;

public interface DgiiAuthPort {
    DgiiTokenResponse obtenerToken(UUID tenantId);
}
