package com.medical.onepay.core.features.invoice.infrastructure.scheduler;

import com.medical.onepay.core.features.invoice.application.usecase.GetInvoiceStatusUseCase;
import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiStatusResponse;
import com.medical.onepay.core.features.invoiceaudit.application.usecase.UpdateInvoiceAuditUseCase;
import com.medical.onepay.core.features.invoiceaudit.domain.model.InvoiceAuditEntity;
import com.medical.onepay.core.features.invoiceaudit.domain.repository.InvoiceAuditRepository;
import com.medical.onepay.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InvoiceStatusScheduler {

    private final InvoiceAuditRepository auditRepository;
    private final GetInvoiceStatusUseCase getInvoiceStatusUseCase;
    private final UpdateInvoiceAuditUseCase updateInvoiceAuditUseCase;

    // Se ejecuta cada hora. cron = "segundo minuto hora día-del-mes mes día-de-la-semana"
    @Scheduled(cron = "0 0 * * * ?")
    public void checkPendingInvoiceStatuses() {
        System.out.println("Iniciando tarea programada: Verificación de estado de facturas pendientes...");

        // Buscamos facturas con estados que podrían cambiar (ej. "SENT", "PROCESANDO")
        List<InvoiceAuditEntity> pendingAudits = auditRepository.findByStatusIn(List.of("SENT", "PROCESANDO"));

        if (pendingAudits.isEmpty()) {
            System.out.println("No hay facturas pendientes para verificar.");
            return;
        }

        System.out.println("Se encontraron " + pendingAudits.size() + " facturas para verificar.");

        for (InvoiceAuditEntity audit : pendingAudits) {
            if (audit.getTrackId() == null || audit.getTenantId() == null) {
                continue; // No podemos consultar sin trackId o tenantId
            }

            try {
                // IMPORTANTE: Establecer el contexto del tenant para cada consulta
                TenantContext.setTenantId(audit.getTenantId());

                System.out.println("Verificando estado para trackId: " + audit.getTrackId());
                DgiiStatusResponse statusResponse = getInvoiceStatusUseCase.execute(audit.getTrackId());

                // Solo actualizamos si el estado ha cambiado
                if (statusResponse != null && !statusResponse.getEstado().equals(audit.getStatus())) {
                    System.out.println("El estado para el trackId " + audit.getTrackId() + " ha cambiado a: " + statusResponse.getEstado());
                    updateInvoiceAuditUseCase.execute(audit.getTrackId(), statusResponse);
                }

            } catch (Exception e) {
                System.err.println("Error al verificar el estado para el trackId " + audit.getTrackId() + ": " + e.getMessage());
            } finally {
                // Limpiar siempre el contexto del tenant
                TenantContext.clear();
            }
        }

        System.out.println("Tarea programada finalizada.");
    }
}
