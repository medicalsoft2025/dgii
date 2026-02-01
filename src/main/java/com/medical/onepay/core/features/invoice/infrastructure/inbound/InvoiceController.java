package com.medical.onepay.core.features.invoice.infrastructure.inbound;

import com.medical.onepay.core.features.invoice.application.usecase.SendInvoice;
import com.medical.onepay.core.features.invoice.application.usecase.GetInvoiceStatusUseCase;
import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiFacturaResponse;
import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final SendInvoice enviarFacturaUseCase;
    private final GetInvoiceStatusUseCase getInvoiceStatusUseCase;

    @PostMapping
    public ResponseEntity<DgiiFacturaResponse> enviarFactura(
            @RequestBody String facturaJson) {
        try {
            DgiiFacturaResponse respuestaDgii = enviarFacturaUseCase.execute(facturaJson);
            return ResponseEntity.ok(respuestaDgii);
        } catch (Exception e) {
            e.printStackTrace();
            // In a real case, a more structured error DTO could be returned.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DgiiFacturaResponse(null, "ERROR", e.getMessage()));
        }
    }

    @GetMapping("/{trackId}/status")
    public ResponseEntity<DgiiStatusResponse> getStatus(@PathVariable String trackId) {
        try {
            DgiiStatusResponse respuestaDgii = getInvoiceStatusUseCase.execute(trackId);
            return ResponseEntity.ok(respuestaDgii);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DgiiStatusResponse.builder()
                            .trackId(trackId)
                            .estado("ERROR")
                            .mensajes(List.of(new DgiiStatusResponse.Mensaje(e.getMessage(), 500)))
                            .build());
        }
    }
}
