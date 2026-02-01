package com.medical.onepay.core.invoice.application.usecase;

import com.medical.onepay.core.features.invoice.application.usecase.*;
import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiFacturaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnviarFacturaUseCaseTest {

    @Mock
    private SendInvoice31UseCase sendInvoice31UseCase;
    @Mock
    private SendInvoice32UseCase sendInvoice32UseCase;
    @Mock
    private SendInvoice33UseCase sendInvoice33UseCase;
    @Mock
    private SendInvoice34UseCase sendInvoice34UseCase;

    private SendInvoice enviarFacturaUseCase;

    @BeforeEach
    void setUp() {
        enviarFacturaUseCase = new SendInvoice(
                sendInvoice31UseCase,
                sendInvoice32UseCase,
                sendInvoice33UseCase,
                sendInvoice34UseCase
        );
    }

    @Test
    void deberiaDespacharAUseCase31() {
        String json = """
            { "ECF": { "Encabezado": { "IdDoc": { "TipoeCF": "31" } } } }
        """;
        
        enviarFacturaUseCase.execute(json);
        
        verify(sendInvoice31UseCase).execute(json);
        verifyNoInteractions(sendInvoice32UseCase, sendInvoice33UseCase, sendInvoice34UseCase);
    }

    @Test
    void deberiaDespacharAUseCase32() {
        String json = """
            { "ECF": { "Encabezado": { "IdDoc": { "TipoeCF": "32" } } } }
        """;
        
        enviarFacturaUseCase.execute(json);
        
        verify(sendInvoice32UseCase).execute(json);
        verifyNoInteractions(sendInvoice31UseCase, sendInvoice33UseCase, sendInvoice34UseCase);
    }

    @Test
    void deberiaDespacharAUseCase33() {
        String json = """
            { "ECF": { "Encabezado": { "IdDoc": { "TipoeCF": "33" } } } }
        """;
        
        enviarFacturaUseCase.execute(json);
        
        verify(sendInvoice33UseCase).execute(json);
        verifyNoInteractions(sendInvoice31UseCase, sendInvoice32UseCase, sendInvoice34UseCase);
    }

    @Test
    void deberiaDespacharAUseCase34() {
        String json = """
            { "ECF": { "Encabezado": { "IdDoc": { "TipoeCF": "34" } } } }
        """;
        
        enviarFacturaUseCase.execute(json);
        
        verify(sendInvoice34UseCase).execute(json);
        verifyNoInteractions(sendInvoice31UseCase, sendInvoice32UseCase, sendInvoice33UseCase);
    }

    @Test
    void deberiaLanzarExcepcionSiTipoNoSoportado() {
        String json = """
            { "ECF": { "Encabezado": { "IdDoc": { "TipoeCF": "99" } } } }
        """;
        
        assertThrows(RuntimeException.class, () -> enviarFacturaUseCase.execute(json));
    }
}
