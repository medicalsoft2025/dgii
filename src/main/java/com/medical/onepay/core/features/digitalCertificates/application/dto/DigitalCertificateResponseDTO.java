package com.medical.onepay.core.features.digitalCertificates.application.dto;

import com.medical.onepay.core.base.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class DigitalCertificateResponseDTO extends BaseDTO {
    private String filename;
    private Instant uploadedAt;
}
