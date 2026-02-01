package com.medical.onepay.core.invoice.application.usecase;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.medical.onepay.config.dgii.DgiiApiProperties;
import com.medical.onepay.core.common.infrastructure.crypto.XmlSignerAdapter;
import com.medical.onepay.core.common.infrastructure.validation.XmlValidatorAdapter;
import com.medical.onepay.core.dgii.ecf.v31.ECF;
import com.medical.onepay.core.features.auth.application.usecase.GetTokenDgiiUseCase;
import com.medical.onepay.core.features.digitalCertificates.domain.model.DigitalCertificateEntity;
import com.medical.onepay.core.features.digitalCertificates.domain.repository.DigitalCertificateRepository;
import com.medical.onepay.core.invoice.infrastructure.adapter.outbound.DgiiInvoiceHttpClientAdapter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnviarFacturaUseCase {

    private final DigitalCertificateRepository digitalCertificateRepository;
    private final XmlSignerAdapter xmlSignerAdapter;
    private final DgiiInvoiceHttpClientAdapter dgiiInvoiceHttpClientAdapter;
    private final GetTokenDgiiUseCase getTokenDgiiUseCase;
    private final DgiiApiProperties dgiiApiProperties;
    private final XmlValidatorAdapter xmlValidator;

    /**
     * Orquesta el proceso completo de envío de una factura electrónica a la DGII.
     */
    public String enviar(String facturaJson, UUID tenantId) {
        try {
            // Paso 1: JSON -> Objeto
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JaxbAnnotationModule());
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

            JsonNode rootNode = objectMapper.readTree(facturaJson);
            JsonNode ecfNode = rootNode.path("ECF");
            ECF ecf = objectMapper.treeToValue(ecfNode, ECF.class);

            // Paso 2: Objeto -> XML
            String xmlSinFirmar = convertirObjetoAXml(ecf);
            if (xmlSinFirmar == null) {
                throw new RuntimeException("No se pudo convertir el objeto a XML.");
            }

            // Paso 3: Firmar el XML
            DigitalCertificateEntity certificate = digitalCertificateRepository.findByTenantId(tenantId)
                    .orElseThrow(() -> new RuntimeException("No se encontró el certificado para el tenant: " + tenantId));

            String xmlFirmado;
            try (InputStream certStream = new ByteArrayInputStream(certificate.getCertificateData())) {
                xmlFirmado = xmlSignerAdapter.sign(xmlSinFirmar, certStream, certificate.getPassword());
            }

            // Paso 3.5: Validar XML Firmado contra XSD
            // Validamos DESPUÉS de firmar porque el XSD exige la presencia de la etiqueta <Signature>.
            String xsdPath = ResourceUtils.getFile("classpath:xsd/ecf31/e-CF 31 v.1.0.xsd").getAbsolutePath();
            xmlValidator.validate(xmlFirmado, xsdPath);

            // Paso 4: Obtener Token de Autenticación
            String token = getTokenDgiiUseCase.obtenerToken(tenantId).getToken();

            // Paso 5: Enviar a la DGII
            String url = dgiiApiProperties.getBaseUrl() + dgiiApiProperties.getEndpoints().getInvoice().getSend();
            String respuestaDgii = dgiiInvoiceHttpClientAdapter.send(xmlFirmado, url, token);

            return respuestaDgii;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en el proceso de envío de factura: " + e.getMessage(), e);
        }
    }

    private String convertirObjetoAXml(Object objetoJaxb) {
        try {
            JAXBContext context = JAXBContext.newInstance("com.medical.onepay.core.dgii.ecf.v31");
            Marshaller marshaller = context.createMarshaller();
            
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            // NOTA: JAXB_FRAGMENT se quita para que el validador reciba un XML completo.
            // marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

            StringWriter sw = new StringWriter();
            marshaller.marshal(objetoJaxb, sw);
            
            return sw.toString();

        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }
}
