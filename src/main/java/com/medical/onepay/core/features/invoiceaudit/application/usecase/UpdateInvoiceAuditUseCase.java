package com.medical.onepay.core.features.invoiceaudit.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiStatusResponse;
import com.medical.onepay.core.features.invoiceaudit.domain.model.InvoiceAuditEntity;
import com.medical.onepay.core.features.invoiceaudit.domain.repository.InvoiceAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateInvoiceAuditUseCase {

    private final InvoiceAuditRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void execute(String trackId, DgiiStatusResponse statusResponse) {
        repository.findByTrackId(trackId).ifPresent(audit -> {
            try {
                audit.setStatus(statusResponse.getEstado());
                audit.setResponseContent(objectMapper.writeValueAsString(statusResponse));
                repository.save(audit);
            } catch (Exception e) {
                // Log the error, but don't let it stop the scheduler
                System.err.println("Error al actualizar la auditoría para el trackId " + trackId + ": " + e.getMessage());
            }
        });
    }
}
