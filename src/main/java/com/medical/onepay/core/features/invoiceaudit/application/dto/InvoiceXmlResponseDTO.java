package com.medical.onepay.core.features.invoiceaudit.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceXmlResponseDTO {
    private String xmlContent;
    private String filename;
}
