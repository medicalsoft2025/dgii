package com.medical.onepay.core.features.digitalCertificates.domain.model;

import com.medical.onepay.core.base.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "digital_certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DigitalCertificateEntity extends BaseModel {

    @Column(name = "certificate_data", nullable = false)
    private byte[] certificateData;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "filename")
    private String filename;

    @Column(name = "uploaded_at")
    private Instant uploadedAt;
}
