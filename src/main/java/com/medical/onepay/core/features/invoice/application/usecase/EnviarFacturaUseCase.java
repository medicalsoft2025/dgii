package com.medical.onepay.core.features.invoice.application.usecase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.onepay.core.features.invoice.application.ports.EnviarFacturaPort;
import com.medical.onepay.core.features.invoice.application.ports.InvoiceSenderStrategy;
import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiFacturaResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EnviarFacturaUseCase implements EnviarFacturaPort {

    private final Map<String, InvoiceSenderStrategy> strategyMap;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EnviarFacturaUseCase(List<InvoiceSenderStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(InvoiceSenderStrategy::getStrategyName, Function.identity()));
    }

    @Override
    public DgiiFacturaResponse execute(String facturaJson) {
        try {
            // Analizar el JSON parcialmente para determinar el tipo de ECF
            JsonNode rootNode = objectMapper.readTree(facturaJson);
            JsonNode ecfNode = rootNode.path("ECF");
            JsonNode encabezadoNode = ecfNode.path("Encabezado");
            JsonNode idDocNode = encabezadoNode.path("IdDoc");
            String tipoEcf = idDocNode.path("TipoeCF").asText();

            // Obtener la estrategia correcta del mapa
            InvoiceSenderStrategy strategy = strategyMap.get(tipoEcf);

            if (strategy != null) {
                return strategy.execute(facturaJson);
            } else {
                throw new IllegalArgumentException("Tipo de e-CF no soportado: " + tipoEcf);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al despachar la factura: " + e.getMessage(), e);
        }
    }
}
