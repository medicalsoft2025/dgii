package com.medical.onepay.core.features.invoice.application.usecase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.onepay.core.features.invoice.application.ports.EnviarFacturaPort;
import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiFacturaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendInvoice implements EnviarFacturaPort {

    private final SendInvoice31UseCase sendInvoice31UseCase;
    private final SendInvoice32UseCase sendInvoice32UseCase;
    private final SendInvoice33UseCase sendInvoice33UseCase;
    private final SendInvoice34UseCase sendInvoice34UseCase;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public DgiiFacturaResponse execute(String facturaJson) {
        try {
            // Analizar el JSON parcialmente para determinar el tipo de ECF
            JsonNode rootNode = objectMapper.readTree(facturaJson);
            JsonNode ecfNode = rootNode.path("ECF");
            JsonNode encabezadoNode = ecfNode.path("Encabezado");
            JsonNode idDocNode = encabezadoNode.path("IdDoc");
            String tipoEcf = idDocNode.path("TipoeCF").asText();

            if ("31".equals(tipoEcf)) {
                log.info("caso 31");
                return sendInvoice31UseCase.execute(facturaJson);
            } else if ("32".equals(tipoEcf)) {
                log.info("Caso 32");
                return sendInvoice32UseCase.execute(facturaJson);
            } else if ("33".equals(tipoEcf)) {
                log.info("Caso 33");
                return sendInvoice33UseCase.execute(facturaJson);
            } else if ("34".equals(tipoEcf)) {
                log.info("Caso 34");
                return sendInvoice34UseCase.execute(facturaJson);
            } else {
                throw new IllegalArgumentException("Tipo de e-CF no soportado: " + tipoEcf);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al despachar la factura: " + e.getMessage(), e);
        }
    }
}
