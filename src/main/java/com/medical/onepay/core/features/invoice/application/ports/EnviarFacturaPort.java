package com.medical.onepay.core.features.invoice.application.ports;

import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiFacturaResponse;

import java.util.UUID;

public interface EnviarFacturaPort {
    DgiiFacturaResponse execute(String facturaJson, UUID tenantId);
}
