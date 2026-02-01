package com.medical.onepay.core.features.invoiceaudit.infrastructure.inbound;

import com.medical.onepay.core.features.invoiceaudit.application.dto.InvoiceXmlResponseDTO;
import com.medical.onepay.core.features.invoiceaudit.application.usecase.GetInvoiceXmlUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
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
