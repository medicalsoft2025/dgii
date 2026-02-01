package com.medical.onepay.core.features.invoice.application.usecase;

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
import com.medical.onepay.core.features.invoice.application.ports.DgiiInvoicePort;
import com.medical.onepay.core.features.invoice.application.ports.EnviarFacturaPort;
import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiFacturaResponse;
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
public class EnviarFacturaUseCase implements EnviarFacturaPort {

    private final DigitalCertificateRepository digitalCertificateRepository;
    private final XmlSignerAdapter xmlSignerAdapter;
    private final DgiiInvoicePort dgiiInvoicePort;
    private final GetTokenDgiiUseCase getTokenDgiiUseCase;
    private final DgiiApiProperties dgiiApiProperties;
    private final XmlValidatorAdapter xmlValidator;

    @Override
    public DgiiFacturaResponse execute(String facturaJson, UUID tenantId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JaxbAnnotationModule());
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

            JsonNode rootNode = objectMapper.readTree(facturaJson);
            JsonNode ecfNode = rootNode.path("ECF");
            ECF ecf = objectMapper.treeToValue(ecfNode, ECF.class);

            String xmlSinFirmar = convertirObjetoAXml(ecf);
            if (xmlSinFirmar == null) {
                throw new RuntimeException("No se pudo convertir el objeto a XML.");
            }

            DigitalCertificateEntity certificate = digitalCertificateRepository.findByTenantId(tenantId)
                    .orElseThrow(() -> new RuntimeException("No se encontró el certificado para el tenant: " + tenantId));

            String xmlFirmado;
            try (InputStream certStream = new ByteArrayInputStream(certificate.getCertificateData())) {
                xmlFirmado = xmlSignerAdapter.sign(xmlSinFirmar, certStream, certificate.getPassword());
            }

            String xsdPath = ResourceUtils.getFile("classpath:xsd/ecf31/e-CF 31 v.1.0.xsd").getAbsolutePath();
            xmlValidator.validate(xmlFirmado, xsdPath);

            String token = getTokenDgiiUseCase.obtenerToken(tenantId).getToken();

            String url = dgiiApiProperties.getBaseUrl() + dgiiApiProperties.getEndpoints().getInvoice().getSend();
            return dgiiInvoicePort.send(xmlFirmado, url, token);

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

            StringWriter sw = new StringWriter();
            marshaller.marshal(objetoJaxb, sw);
            
            return sw.toString();

        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }
}
