package com.medical.onepay.core.features.invoiceaudit.infrastructure.inbound;

import com.medical.onepay.core.features.invoiceaudit.application.dto.InvoiceAuditResponseDTO;
import com.medical.onepay.core.features.invoiceaudit.application.dto.InvoiceXmlResponseDTO;
import com.medical.onepay.core.features.invoiceaudit.application.mapper.InvoiceAuditMapper;
import com.medical.onepay.core.features.invoiceaudit.application.usecase.GetAllInvoiceAuditsUseCase;
import com.medical.onepay.core.features.invoiceaudit.application.usecase.GetInvoiceXmlUseCase;
import com.medical.onepay.core.features.invoiceaudit.domain.model.InvoiceAuditEntity;
import com.medical.onepay.shared.responses.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoices/audit")
@RequiredArgsConstructor
public class InvoiceAuditController {

    private final GetInvoiceXmlUseCase getInvoiceXmlUseCase;
    private final GetAllInvoiceAuditsUseCase getAllInvoiceAuditsUseCase;
    private final InvoiceAuditMapper mapper;

    @GetMapping
    public ResponseEntity<?> getAll(@PageableDefault(size = 10) Pageable pageable) {
        Page<InvoiceAuditEntity> page = getAllInvoiceAuditsUseCase.execute(pageable);
        Page<InvoiceAuditResponseDTO> responsePage = page.map(mapper::toResponse);
        return ResponseUtil.paged(responsePage);
    }

    @GetMapping("/{id}/xml")
    public ResponseEntity<Resource> downloadXml(@PathVariable UUID id) {
        InvoiceXmlResponseDTO response = getInvoiceXmlUseCase.execute(id);

        ByteArrayResource resource = new ByteArrayResource(response.getXmlContent().getBytes(StandardCharsets.UTF_8));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_XML)
                .contentLength(resource.contentLength())
                .body(resource);
    }
}
