package com.medical.onepay.core.features.invoiceaudit.application.usecase;

import com.medical.onepay.core.features.invoiceaudit.domain.model.InvoiceAuditEntity;
import com.medical.onepay.core.features.invoiceaudit.domain.repository.InvoiceAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllInvoiceAuditsUseCase {

    private final InvoiceAuditRepository repository;

    @Transactional(readOnly = true)
    public Page<InvoiceAuditEntity> execute(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
