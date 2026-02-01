package com.medical.onepay.core.features.invoice.application.ports;

import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiFacturaResponse;

public interface InvoiceSenderStrategy {
    DgiiFacturaResponse execute(String facturaJson);
    String getStrategyName();
}
