package com.medical.onepay.core.features.invoiceaudit.application.usecase;

import com.medical.onepay.core.features.invoiceaudit.domain.model.InvoiceAuditEntity;
import com.medical.onepay.core.features.invoiceaudit.domain.repository.InvoiceAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CreateInvoiceAuditUseCase {

    private final InvoiceAuditRepository repository;

    @Transactional
    public void execute(String xmlContent, String encf, String status, String responseContent, String trackId) {
        InvoiceAuditEntity audit = new InvoiceAuditEntity();
        audit.setXmlContent(xmlContent);
        audit.setEncf(encf);
        audit.setSentAt(Instant.now());
        audit.setStatus(status);
        audit.setResponseContent(responseContent);
        audit.setTrackId(trackId);
        
        repository.save(audit);
    }
}
