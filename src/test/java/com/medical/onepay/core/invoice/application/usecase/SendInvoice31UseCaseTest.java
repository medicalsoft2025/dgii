package com.medical.onepay.core.invoice.application.usecase;

import com.medical.onepay.config.dgii.DgiiApiProperties;
import com.medical.onepay.core.common.infrastructure.crypto.XmlSignerAdapter;
import com.medical.onepay.core.common.infrastructure.validation.XmlValidatorAdapter;
import com.medical.onepay.core.features.auth.application.usecase.GetTokenDgiiUseCase;
import com.medical.onepay.core.features.auth.infrastructure.dto.DgiiTokenResponse;
import com.medical.onepay.core.features.digitalCertificates.domain.model.DigitalCertificateEntity;
import com.medical.onepay.core.features.digitalCertificates.domain.repository.DigitalCertificateRepository;
import com.medical.onepay.core.features.invoice.application.ports.DgiiInvoicePort;
import com.medical.onepay.core.features.invoice.application.usecase.SendInvoice31UseCase;
import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiFacturaResponse;
import com.medical.onepay.core.features.invoiceaudit.application.usecase.CreateInvoiceAuditUseCase;
import com.medical.onepay.shared.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendInvoice31UseCaseTest {

    @Mock
    private DigitalCertificateRepository digitalCertificateRepository;
    @Mock
    private XmlSignerAdapter xmlSignerAdapter;
    @Mock
    private DgiiInvoicePort dgiiInvoicePort;
    @Mock
    private GetTokenDgiiUseCase getTokenDgiiUseCase;
    @Mock
    private DgiiApiProperties dgiiApiProperties;
    @Mock
    private XmlValidatorAdapter xmlValidator;
    @Mock
    private CreateInvoiceAuditUseCase createInvoiceAuditUseCase;

    private SendInvoice31UseCase sendInvoice31UseCase;

    @BeforeEach
    void setUp() {
        sendInvoice31UseCase = new SendInvoice31UseCase(
                digitalCertificateRepository,
                xmlSignerAdapter,
                dgiiInvoicePort,
                getTokenDgiiUseCase,
                dgiiApiProperties,
                xmlValidator,
                createInvoiceAuditUseCase
        );
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void deberiaConvertirJsonAXmlCorrectamente() throws Exception {
        // 1. Arrange
        String facturaJson = """
        {
          "ECF": {
            "Encabezado": {
              "Version": "1.0",
              "IdDoc": {
                "TipoeCF": "31",
                "eNCF": "E310000000001",
                "FechaVencimientoSecuencia": "31-12-2025",
                "IndicadorMontoGravado": 0,
                "TipoIngresos": "01",
                "TipoPago": 1
              },
              "Emisor": {
                "RNCEmisor": "123003994",
                "RazonSocialEmisor": "DOCUMENTOS ELECTRONICOS DE 02",
                "NombreComercial": "DOCUMENTOS ELECTRONICOS DE 02",
                "DireccionEmisor": "AVE. ISABEL AGUIAR NO. 269, ZONA INDUSTRIAL DE HERRERA",
                "Municipio": "010101",
                "Provincia": "010000",
                "TablaTelefonoEmisor": {
                  "TelefonoEmisor": ["809-472-7676", "809-491-1918"]
                },
                "CorreoEmisor": "test@test.com",
                "WebSite": "www.facturaelectronica.com",
                "CodigoVendedor": "AA001",
                "NumeroFacturaInterna": "123456789016",
                "NumeroPedidoInterno": "123456789016",
                "ZonaVenta": "NORT",
                "FechaEmision": "01-04-2020"
              },
              "Comprador": {
                "RNCComprador": "131880681",
                "RazonSocialComprador": "DOCUMENTOS ELECTRONICOS DE 03",
                "ContactoComprador": "MARCOS LATIPLOL",
                "CorreoComprador": "test@test.com",
                "DireccionComprador": "CALLE JACINTO",
                "MunicipioComprador": "010100",
                "ProvinciaComprador": "010000",
                "FechaEntrega": "10-10-2020",
                "FechaOrdenCompra": "10-11-2018",
                "NumeroOrdenCompra": "4500352238",
                "CodigoInternoComprador": "10633440"
              },
              "Totales": {
                "MontoGravadoTotal": 6000.00,
                "MontoGravadoI1": 6000.00,
                "ITBIS1": 18,
                "TotalITBIS": 1080.00,
                "TotalITBIS1": 1080.00,
                "MontoTotal": 7080.00
              }
            },
            "DetallesItems": {
              "Item": {
                "NumeroLinea": 1,
                "IndicadorFacturacion": 1,
                "NombreItem": "ASW DTU",
                "IndicadorBienoServicio": 1,
                "CantidadItem": 15.00,
                "UnidadMedida": 31,
                "PrecioUnitarioItem": 400.00,
                "MontoItem": 6000.00
              }
            },
            "FechaHoraFirma": "03-09-2025 15:22:10"
          }
        }
        """;

        UUID tenantId = UUID.randomUUID();
        TenantContext.setTenantId(tenantId);

        DigitalCertificateEntity certificate = new DigitalCertificateEntity();
        certificate.setCertificateData(new byte[0]);
        certificate.setPassword("password");
        when(digitalCertificateRepository.findByTenantId(tenantId)).thenReturn(Optional.of(certificate));

        when(xmlSignerAdapter.sign(anyString(), any(InputStream.class), anyString())).thenReturn("<xml>firmado</xml>");
        
        DgiiTokenResponse tokenResponse = new DgiiTokenResponse("token", null, null);
        when(getTokenDgiiUseCase.obtenerToken(tenantId)).thenReturn(tokenResponse);

        DgiiApiProperties.Endpoints endpoints = new DgiiApiProperties.Endpoints();
        DgiiApiProperties.Invoice invoice = new DgiiApiProperties.Invoice();
        invoice.setSend("/invoice");
        endpoints.setInvoice(invoice);
        when(dgiiApiProperties.getEndpoints()).thenReturn(endpoints);
        when(dgiiApiProperties.getBaseUrl()).thenReturn("http://localhost");

        when(dgiiInvoicePort.send(anyString(), anyString(), anyString())).thenReturn(new DgiiFacturaResponse());

        // 2. Act
        sendInvoice31UseCase.execute(facturaJson);

        // 3. Assert
        ArgumentCaptor<String> xmlCaptor = ArgumentCaptor.forClass(String.class);
        verify(xmlSignerAdapter).sign(xmlCaptor.capture(), any(InputStream.class), anyString());
        
        String resultadoXml = xmlCaptor.getValue();
        assertNotNull(resultadoXml);
        assertTrue(resultadoXml.contains("<ECF>"));
        assertTrue(resultadoXml.contains("<eNCF>E310000000001</eNCF>"));

        // Verify audit is called
        verify(createInvoiceAuditUseCase).execute(anyString(), any(), anyString(), anyString());
    }
}
