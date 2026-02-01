package com.medical.onepay.core.features.invoiceaudit.domain.repository;

import com.medical.onepay.core.features.invoiceaudit.domain.model.InvoiceAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceAuditRepository extends JpaRepository<InvoiceAuditEntity, UUID> {

    Optional<InvoiceAuditEntity> findByTrackId(String trackId);

    List<InvoiceAuditEntity> findByStatusIn(List<String> statuses);
}
