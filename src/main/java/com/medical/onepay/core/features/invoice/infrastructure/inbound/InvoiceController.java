package com.medical.onepay.core.features.invoice.infrastructure.inbound;

import com.medical.onepay.core.features.invoice.application.usecase.SendInvoice;
import com.medical.onepay.core.features.invoice.application.usecase.SendInvoice31UseCase;
import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiFacturaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final SendInvoice sendInvoice;

    @PostMapping
    public ResponseEntity<DgiiFacturaResponse> enviarFactura(
            @RequestBody String facturaJson) {
        try {
            DgiiFacturaResponse respuestaDgii = sendInvoice.execute(facturaJson);
            return ResponseEntity.ok(respuestaDgii);
        } catch (Exception e) {
            e.printStackTrace();
            // In a real case, a more structured error DTO could be returned.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DgiiFacturaResponse(null, "ERROR", e.getMessage()));
        }
    }
}
