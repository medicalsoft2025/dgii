package com.medical.onepay.core.features.invoiceaudit.application.mapper;

import com.medical.onepay.core.features.invoiceaudit.application.dto.InvoiceAuditRequestDTO;
import com.medical.onepay.core.features.invoiceaudit.application.dto.InvoiceAuditResponseDTO;
import com.medical.onepay.core.features.invoiceaudit.domain.model.InvoiceAuditEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceAuditMapper {

    InvoiceAuditEntity toEntity(InvoiceAuditRequestDTO dto);

    InvoiceAuditResponseDTO toResponse(InvoiceAuditEntity entity);
}
