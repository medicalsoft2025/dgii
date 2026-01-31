package com.medical.onepay.core.features.digitalCertificates.infrastructure.outbound;

import com.medical.onepay.core.base.BaseRepository;
import com.medical.onepay.core.features.digitalCertificates.domain.model.DigitalCertificateEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DigitalCertificateJpaRepository extends BaseRepository<DigitalCertificateEntity, UUID> {
    Optional<DigitalCertificateEntity> findByTenantId(UUID tenantId);
}
