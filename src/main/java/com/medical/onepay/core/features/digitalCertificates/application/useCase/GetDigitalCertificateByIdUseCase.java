package com.medical.onepay.core.features.digitalCertificates.application.useCase;

import com.medical.onepay.core.features.digitalCertificates.domain.model.DigitalCertificateEntity;
import com.medical.onepay.core.features.digitalCertificates.domain.repository.DigitalCertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetDigitalCertificateByIdUseCase {

    private final DigitalCertificateRepository repository;

    public DigitalCertificateEntity execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Digital Certificate not found with id: " + id));
    }
}
