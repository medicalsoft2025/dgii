package com.medical.onepay.core.features.digitalCertificates.infrastructure.outbound;

import com.medical.onepay.core.features.digitalCertificates.domain.model.DigitalCertificateEntity;
import com.medical.onepay.core.features.digitalCertificates.domain.repository.DigitalCertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DigitalCertificateRepositoryImpl implements DigitalCertificateRepository {

    private final DigitalCertificateJpaRepository jpaRepository;

    @Override
    public DigitalCertificateEntity save(DigitalCertificateEntity certificate) {
        return jpaRepository.save(certificate);
    }

    @Override
    public Optional<DigitalCertificateEntity> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<DigitalCertificateEntity> findByTenantId(UUID tenantId) {
        return jpaRepository.findByTenantId(tenantId);
    }

    @Override
    public Page<DigitalCertificateEntity> findAll(Pageable pageable) {
        return jpaRepository.findAllByActiveTrue(pageable);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
