package com.medical.onepay.core.features.invoiceaudit.application.dto;

import com.medical.onepay.core.base.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
public class InvoiceAuditRequestDTO extends BaseDTO {
    private String xmlContent;
    private String encf;
    private Instant sentAt;
    private String status;
    private String responseContent;
    private String trackId;
}
