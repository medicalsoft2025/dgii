package com.medical.onepay.core.features.invoice.infrastructure.inbound;

import com.medical.onepay.config.rabbitmq.RabbitMQConfig;
import com.medical.onepay.core.features.invoice.application.usecase.GetInvoiceStatusUseCase;
import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiStatusResponse;
import com.medical.onepay.core.features.invoice.infrastructure.rabbitmq.InvoiceMessageDTO;
import com.medical.onepay.shared.responses.ApiResponse;
import com.medical.onepay.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final GetInvoiceStatusUseCase getInvoiceStatusUseCase;
    private final RabbitTemplate rabbitTemplate;

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> enviarFactura(
            @RequestBody String facturaJson) {
        try {
            UUID tenantId = TenantContext.getTenantId();
            InvoiceMessageDTO message = new InvoiceMessageDTO(tenantId, facturaJson);

            rabbitTemplate.convertAndSend(RabbitMQConfig.INVOICE_EXCHANGE, RabbitMQConfig.INVOICE_ROUTING_KEY, message);

            ApiResponse<Object> apiResponse = new ApiResponse<>(HttpStatus.ACCEPTED.value(), "Factura recibida y encolada para procesamiento.", null);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(apiResponse);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Object> errorResponse = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al encolar la factura: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
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
