package com.medical.onepay.core.invoice.infrastructure.inbound;

import com.medical.onepay.core.invoice.application.usecase.EnviarFacturaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final EnviarFacturaUseCase enviarFacturaUseCase;

    @PostMapping("/{tenantId}")
    public ResponseEntity<String> enviarFactura(
            @PathVariable UUID tenantId,
            @RequestBody String facturaJson) {
        try {
            String respuestaDgii = enviarFacturaUseCase.enviar(facturaJson, tenantId);
            return ResponseEntity.ok(respuestaDgii);
        } catch (Exception e) {
            // En un caso real, se podría devolver un DTO de error más estructurado.
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error procesando la factura: " + e.getMessage());
        }
    }
}
