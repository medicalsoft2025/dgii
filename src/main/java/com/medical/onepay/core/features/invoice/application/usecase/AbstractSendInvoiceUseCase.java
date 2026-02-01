package com.medical.onepay.core.features.invoice.application.usecase;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.medical.onepay.config.dgii.DgiiApiProperties;
import com.medical.onepay.core.common.infrastructure.crypto.XmlSignerAdapter;
import com.medical.onepay.core.common.infrastructure.validation.XmlValidatorAdapter;
import com.medical.onepay.core.features.auth.application.usecase.GetTokenDgiiUseCase;
import com.medical.onepay.core.features.digitalCertificates.domain.model.DigitalCertificateEntity;
import com.medical.onepay.core.features.digitalCertificates.domain.repository.DigitalCertificateRepository;
import com.medical.onepay.core.features.invoice.application.ports.DgiiInvoicePort;
import com.medical.onepay.core.features.invoice.application.ports.InvoiceSenderStrategy;
import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiFacturaResponse;
import com.medical.onepay.core.features.invoiceaudit.application.usecase.CreateInvoiceAuditUseCase;
import com.medical.onepay.shared.tenant.TenantContext;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public abstract class AbstractSendInvoiceUseCase implements InvoiceSenderStrategy {

    protected final DigitalCertificateRepository digitalCertificateRepository;
    protected final XmlSignerAdapter xmlSignerAdapter;
    protected final DgiiInvoicePort dgiiInvoicePort;
    protected final GetTokenDgiiUseCase getTokenDgiiUseCase;
    protected final DgiiApiProperties dgiiApiProperties;
    protected final XmlValidatorAdapter xmlValidator;
    protected final CreateInvoiceAuditUseCase createInvoiceAuditUseCase;

    // Métodos abstractos que deben implementar las subclases
    protected abstract Object mapJsonToEcf(String json, ObjectMapper objectMapper) throws Exception;
    protected abstract String getJaxbContextPath();
    protected abstract String getXsdPath();

    @Override
    public DgiiFacturaResponse execute(String facturaJson) {
        String xmlFirmado = null;
        String encf = null;
        try {
            UUID tenantId = TenantContext.getTenantId();

            // Configuración común del ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JaxbAnnotationModule());
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

            // 1. Convertir JSON a Objeto Java (Delegado a la subclase)
            Object ecfObject = mapJsonToEcf(facturaJson, objectMapper);

            // 2. Convertir Objeto a XML (Usando el contexto específico de la subclase)
            String xmlSinFirmar = convertirObjetoAXml(ecfObject, getJaxbContextPath());
            if (xmlSinFirmar == null) {
                throw new RuntimeException("No se pudo convertir el objeto a XML.");
            }

            // 3. Obtener Certificado
            DigitalCertificateEntity certificate = digitalCertificateRepository.findByTenantId(tenantId)
                    .orElseThrow(() -> new RuntimeException("No se encontró el certificado para el tenant: " + tenantId));

            // 4. Firmar XML
            try (InputStream certStream = new ByteArrayInputStream(certificate.getCertificateData())) {
                xmlFirmado = xmlSignerAdapter.sign(xmlSinFirmar, certStream, certificate.getPassword());
            }

            // Extraer eNCF para auditoría
            encf = extractEncf(xmlFirmado);

            // 5. Validar contra XSD (Usando el path específico de la subclase)
            String xsdAbsolutePath = ResourceUtils.getFile(getXsdPath()).getAbsolutePath();
            xmlValidator.validate(xmlFirmado, xsdAbsolutePath);

            // 6. Obtener Token
            String token = getTokenDgiiUseCase.obtenerToken(tenantId).getToken();

            // 7. Enviar a DGII
            String url = dgiiApiProperties.getBaseUrl() + dgiiApiProperties.getEndpoints().getInvoice().getSend();
            DgiiFacturaResponse response = dgiiInvoicePort.send(xmlFirmado, url, token);

            // 8. Auditar éxito
            createInvoiceAuditUseCase.execute(xmlFirmado, encf, "SENT", response.toString(), response.getTrackId());

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            // 9. Auditar error
            if (xmlFirmado != null) {
                createInvoiceAuditUseCase.execute(xmlFirmado, encf, "ERROR", e.getMessage(), null);
            }
            throw new RuntimeException("Error en el proceso de envío de factura: " + e.getMessage(), e);
        }
    }

    private String convertirObjetoAXml(Object objetoJaxb, String contextPath) {
        try {
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Marshaller marshaller = context.createMarshaller();
            
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            StringWriter sw = new StringWriter();
            marshaller.marshal(objetoJaxb, sw);
            
            return sw.toString();

        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractEncf(String xml) {
        if (xml == null) return null;
        Pattern pattern = Pattern.compile("<eNCF>(.*?)</eNCF>");
        Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
