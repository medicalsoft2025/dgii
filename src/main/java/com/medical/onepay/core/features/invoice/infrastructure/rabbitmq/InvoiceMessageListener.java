package com.medical.onepay.core.features.invoice.infrastructure.rabbitmq;

import com.medical.onepay.config.rabbitmq.RabbitMQConfig;
import com.medical.onepay.core.features.invoice.application.usecase.SendInvoice;
import com.medical.onepay.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvoiceMessageListener {

    private final SendInvoice enviarFacturaUseCase;

    @RabbitListener(queues = RabbitMQConfig.INVOICE_QUEUE)
    public void handleInvoiceMessage(InvoiceMessageDTO message) {
        System.out.println("Recibido mensaje de factura para procesamiento asíncrono. Tenant: " + message.getTenantId());
        
        try {
            // Establecer el contexto del tenant para este hilo de ejecución
            TenantContext.setTenantId(message.getTenantId());

            // El UseCase ya obtiene el tenantId del TenantContext
            enviarFacturaUseCase.execute(message.getFacturaJson());
            System.out.println("Factura procesada y enviada a la DGII exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al procesar factura asíncronamente: " + e.getMessage());
            // Si se lanza una excepción aquí, RabbitMQ moverá el mensaje a la DLQ
            throw new RuntimeException("Fallo en el procesamiento de la factura", e);
        } finally {
            // Es crucial limpiar el TenantContext después de cada procesamiento
            TenantContext.clear();
        }
    }
}
