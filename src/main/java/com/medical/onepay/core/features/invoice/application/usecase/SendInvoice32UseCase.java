package com.medical.onepay.core.features.invoice.application.usecase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.onepay.config.dgii.DgiiApiProperties;
import com.medical.onepay.core.common.infrastructure.crypto.XmlSignerAdapter;
import com.medical.onepay.core.common.infrastructure.validation.XmlValidatorAdapter;
import com.medical.onepay.core.dgii.ecf.v32.ECF;
import com.medical.onepay.core.features.auth.application.usecase.GetTokenDgiiUseCase;
import com.medical.onepay.core.features.digitalCertificates.domain.repository.DigitalCertificateRepository;
import com.medical.onepay.core.features.invoice.application.ports.DgiiInvoicePort;
import com.medical.onepay.core.features.invoiceaudit.application.usecase.CreateInvoiceAuditUseCase;
import org.springframework.stereotype.Service;

@Service("sendInvoice32Strategy")
public class SendInvoice32UseCase extends AbstractSendInvoiceUseCase {

    public SendInvoice32UseCase(
            DigitalCertificateRepository digitalCertificateRepository,
            XmlSignerAdapter xmlSignerAdapter,
            DgiiInvoicePort dgiiInvoicePort,
            GetTokenDgiiUseCase getTokenDgiiUseCase,
            DgiiApiProperties dgiiApiProperties,
            XmlValidatorAdapter xmlValidator,
            CreateInvoiceAuditUseCase createInvoiceAuditUseCase) {
        super(digitalCertificateRepository, xmlSignerAdapter, dgiiInvoicePort, getTokenDgiiUseCase, dgiiApiProperties, xmlValidator, createInvoiceAuditUseCase);
    }

    @Override
    protected Object mapJsonToEcf(String json, ObjectMapper objectMapper) throws Exception {
        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode ecfNode = rootNode.path("ECF");
        return objectMapper.treeToValue(ecfNode, ECF.class);
    }

    @Override
    protected String getJaxbContextPath() {
        return "com.medical.onepay.core.dgii.ecf.v32";
    }

    @Override
    protected String getXsdPath() {
        return "classpath:xsd/ecf32/e-CF 32 v.1.0.xsd";
    }

    @Override
    public String getStrategyName() {
        return "32";
    }
}
