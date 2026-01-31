package com.medical.onepay.core.features.digitalCertificates.application.useCase;

import com.medical.onepay.core.features.digitalCertificates.domain.repository.DigitalCertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteDigitalCertificateUseCase {

    private final DigitalCertificateRepository repository;

    public void execute(UUID id) {
        // First check if it exists
        repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Digital Certificate not found with id: " + id));
        repository.deleteById(id);
    }
}
