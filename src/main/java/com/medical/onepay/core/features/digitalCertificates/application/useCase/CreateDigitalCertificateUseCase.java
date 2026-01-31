package com.medical.onepay.core.features.digitalCertificates.application.useCase;

import com.medical.onepay.core.features.digitalCertificates.domain.model.DigitalCertificateEntity;
import com.medical.onepay.core.features.digitalCertificates.domain.repository.DigitalCertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CreateDigitalCertificateUseCase {

    private final DigitalCertificateRepository repository;

    public DigitalCertificateEntity execute(MultipartFile file, String password) throws IOException {
        DigitalCertificateEntity certificate = new DigitalCertificateEntity();
        certificate.setCertificateData(file.getBytes());
        certificate.setPassword(password); // Remember to encrypt this in a real app
        certificate.setFilename(file.getOriginalFilename());
        certificate.setUploadedAt(Instant.now());
        return repository.save(certificate);
    }
}
