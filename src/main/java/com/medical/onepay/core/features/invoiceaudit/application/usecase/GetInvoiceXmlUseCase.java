package com.medical.onepay.core.features.invoiceaudit.application.usecase;

import com.medical.onepay.core.features.invoiceaudit.application.dto.InvoiceXmlResponseDTO;
import com.medical.onepay.core.features.invoiceaudit.domain.model.InvoiceAuditEntity;
import com.medical.onepay.core.features.invoiceaudit.domain.repository.InvoiceAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetInvoiceXmlUseCase {

    private final InvoiceAuditRepository repository;

    @Transactional(readOnly = true)
    public InvoiceXmlResponseDTO execute(UUID id) {
        InvoiceAuditEntity audit = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));

        if (audit.getXmlContent() == null || audit.getXmlContent().isEmpty()) {
            throw new RuntimeException("El contenido XML no está disponible para esta factura.");
        }

        String filename = (audit.getEncf() != null ? audit.getEncf() : "factura") + ".xml";

        return new InvoiceXmlResponseDTO(audit.getXmlContent(), filename);
    }
}
