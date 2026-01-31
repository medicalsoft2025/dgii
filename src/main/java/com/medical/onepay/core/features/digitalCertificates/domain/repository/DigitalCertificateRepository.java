package com.medical.onepay.core.features.digitalCertificates.domain.repository;

import com.medical.onepay.core.features.digitalCertificates.domain.model.DigitalCertificateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface DigitalCertificateRepository {
    DigitalCertificateEntity save(DigitalCertificateEntity certificate);
    Optional<DigitalCertificateEntity> findById(UUID id);
    Optional<DigitalCertificateEntity> findByTenantId(UUID tenantId);
    Page<DigitalCertificateEntity> findAll(Pageable pageable);
    void deleteById(UUID id);
}
