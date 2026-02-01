package com.medical.onepay.core.features.invoice.infrastructure.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceMessageDTO implements Serializable {
    private UUID tenantId;
    private String facturaJson;
}
