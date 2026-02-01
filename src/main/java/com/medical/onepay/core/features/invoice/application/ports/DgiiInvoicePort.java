package com.medical.onepay.core.features.invoice.application.ports;

import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiFacturaResponse;

public interface DgiiInvoicePort {
    DgiiFacturaResponse send(String signedXml, String url, String token);
}
