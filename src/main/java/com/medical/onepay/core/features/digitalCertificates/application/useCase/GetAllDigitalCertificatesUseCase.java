package com.medical.onepay.core.features.digitalCertificates.application.useCase;

import com.medical.onepay.core.features.digitalCertificates.domain.model.DigitalCertificateEntity;
import com.medical.onepay.core.features.digitalCertificates.domain.repository.DigitalCertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllDigitalCertificatesUseCase {

    private final DigitalCertificateRepository repository;

    public Page<DigitalCertificateEntity> execute(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
