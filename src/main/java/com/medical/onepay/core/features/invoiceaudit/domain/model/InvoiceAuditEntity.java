package com.medical.onepay.core.features.invoiceaudit.domain.model;

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
@Table(name = "invoice_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceAuditEntity extends BaseModel {

    @Column(name = "xml_content", columnDefinition = "TEXT")
    private String xmlContent;

    @Column(name = "encf", length = 20)
    private String encf;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "response_content", columnDefinition = "TEXT")
    private String responseContent;

    @Column(name = "track_id", length = 100)
    private String trackId;
}
