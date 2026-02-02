package com.medical.onepay.core.invoice.application.usecase;

import com.medical.onepay.config.rabbitmq.RabbitMQConfig;
import com.medical.onepay.core.features.invoice.application.usecase.EnviarFacturaUseCase;
import com.medical.onepay.core.features.invoice.infrastructure.rabbitmq.InvoiceMessageDTO;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@Testcontainers
class InvoiceMessageListenerIT {

    @Container
    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.13-management");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // Reemplazo de @MockBean por @MockitoBean para versiones recientes de Spring Boot
    @MockitoBean
    private EnviarFacturaUseCase enviarFacturaUseCase;

    @Test
    void cuandoProcesamientoFalla_mensajeDebeIrADeadLetterQueue() {
        // 1. Arrange: Simular que el envío a la DGII falla
        doThrow(new RuntimeException("Simulación: DGII no disponible"))
                .when(enviarFacturaUseCase).execute(any(String.class));

        // Crear un mensaje de prueba
        UUID tenantId = UUID.randomUUID();
        String facturaJson = "{\"ECF\": {\"Encabezado\": {\"IdDoc\": {\"TipoeCF\": \"31\"}}}}";
        InvoiceMessageDTO message = new InvoiceMessageDTO(tenantId, facturaJson);

        // 2. Act: Enviar el mensaje a la cola principal
        rabbitTemplate.convertAndSend(RabbitMQConfig.INVOICE_EXCHANGE, RabbitMQConfig.INVOICE_ROUTING_KEY, message);

        // Esperar un poco para que el listener procese y falle
        try {
            Thread.sleep(2000); // 2 segundos deberían ser suficientes
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 3. Assert: Verificar que el mensaje está en la Dead Letter Queue
        InvoiceMessageDTO failedMessage = (InvoiceMessageDTO) rabbitTemplate.receiveAndConvert(RabbitMQConfig.DEAD_LETTER_QUEUE);

        assertNotNull(failedMessage, "El mensaje no debería ser nulo en la DLQ");
        assertEquals(tenantId, failedMessage.getTenantId(), "El tenantId del mensaje fallido debe coincidir");
        assertEquals(facturaJson, failedMessage.getFacturaJson(), "El contenido JSON del mensaje fallido debe coincidir");
    }
}
