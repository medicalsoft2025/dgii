package com.medical.onepay.core.features.digitalCertificates.infrastructure.inbound;

import com.medical.onepay.core.features.digitalCertificates.application.dto.DigitalCertificateResponseDTO;
import com.medical.onepay.core.features.digitalCertificates.application.mapper.DigitalCertificateMapper;
import com.medical.onepay.core.features.digitalCertificates.application.useCase.CreateDigitalCertificateUseCase;
import com.medical.onepay.core.features.digitalCertificates.application.useCase.DeleteDigitalCertificateUseCase;
import com.medical.onepay.core.features.digitalCertificates.application.useCase.GetAllDigitalCertificatesUseCase;
import com.medical.onepay.core.features.digitalCertificates.application.useCase.GetDigitalCertificateByIdUseCase;
import com.medical.onepay.core.features.digitalCertificates.domain.model.DigitalCertificateEntity;
import com.medical.onepay.shared.responses.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class DigitalCertificateController {

    private final CreateDigitalCertificateUseCase createUseCase;
    private final GetAllDigitalCertificatesUseCase getAllUseCase;
    private final GetDigitalCertificateByIdUseCase getByIdUseCase;
    private final DeleteDigitalCertificateUseCase deleteUseCase;
    private final DigitalCertificateMapper mapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCertificate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password) {
        try {
            DigitalCertificateEntity saved = createUseCase.execute(file, password);
            return ResponseUtil.created(mapper.toResponse(saved));
        } catch (IOException e) {
            return ResponseUtil.custom(HttpStatus.INTERNAL_SERVER_ERROR,"Error processing file: " + e.getMessage(), null);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCertificates(@PageableDefault(size = 10) Pageable pageable) {
        Page<DigitalCertificateEntity> page = getAllUseCase.execute(pageable);
        Page<DigitalCertificateResponseDTO> responsePage = page.map(mapper::toResponse);
        return ResponseUtil.paged(responsePage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCertificateById(@PathVariable UUID id) {
        DigitalCertificateEntity certificate = getByIdUseCase.execute(id);
        return ResponseUtil.ok(mapper.toResponse(certificate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCertificate(@PathVariable UUID id) {
        deleteUseCase.execute(id);
        return ResponseUtil.noContent();
    }
}
