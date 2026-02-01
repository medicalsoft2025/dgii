package com.medical.onepay.core.features.invoice.application.usecase;

import com.medical.onepay.config.dgii.DgiiApiProperties;
import com.medical.onepay.core.features.auth.application.usecase.GetTokenDgiiUseCase;
import com.medical.onepay.core.features.invoice.application.ports.DgiiStatusPort;
import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiStatusResponse;
import com.medical.onepay.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetInvoiceStatusUseCase {

    private final DgiiStatusPort dgiiStatusPort;
    private final GetTokenDgiiUseCase getTokenDgiiUseCase;
    private final DgiiApiProperties dgiiApiProperties;

    public DgiiStatusResponse execute(String trackId) {
        UUID tenantId = TenantContext.getTenantId();
        
        // 1. Obtener Token
        String token = getTokenDgiiUseCase.obtenerToken(tenantId).getToken();

        // 2. Construir URL
        String url = dgiiApiProperties.getBaseUrl() + dgiiApiProperties.getEndpoints().getStatus().getTrack();

        // 3. Consultar estado
        return dgiiStatusPort.checkStatus(trackId, url, token);
    }
}
