package com.medical.onepay.core.features.invoice.application.usecase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.onepay.config.dgii.DgiiApiProperties;
import com.medical.onepay.core.common.infrastructure.crypto.XmlSignerAdapter;
import com.medical.onepay.core.common.infrastructure.validation.XmlValidatorAdapter;
import com.medical.onepay.core.dgii.ecf.v31.ECF;
import com.medical.onepay.core.features.auth.application.usecase.GetTokenDgiiUseCase;
import com.medical.onepay.core.features.digitalCertificates.domain.repository.DigitalCertificateRepository;
import com.medical.onepay.core.features.invoice.application.ports.DgiiInvoicePort;
import org.springframework.stereotype.Service;

@Service
public class SendInvoice31UseCase extends AbstractSendInvoiceUseCase {

    public SendInvoice31UseCase(
            DigitalCertificateRepository digitalCertificateRepository,
            XmlSignerAdapter xmlSignerAdapter,
            DgiiInvoicePort dgiiInvoicePort,
            GetTokenDgiiUseCase getTokenDgiiUseCase,
            DgiiApiProperties dgiiApiProperties,
            XmlValidatorAdapter xmlValidator) {
        super(digitalCertificateRepository, xmlSignerAdapter, dgiiInvoicePort, getTokenDgiiUseCase, dgiiApiProperties, xmlValidator);
    }

    @Override
    protected Object mapJsonToEcf(String json, ObjectMapper objectMapper) throws Exception {
        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode ecfNode = rootNode.path("ECF");
        return objectMapper.treeToValue(ecfNode, ECF.class);
    }

    @Override
    protected String getJaxbContextPath() {
        return "com.medical.onepay.core.dgii.ecf.v31";
    }

    @Override
    protected String getXsdPath() {
        return "classpath:xsd/ecf31/e-CF 31 v.1.0.xsd";
    }
}
