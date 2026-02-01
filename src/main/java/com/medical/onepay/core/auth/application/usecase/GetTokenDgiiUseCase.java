package com.medical.onepay.core.auth.application.usecase;

import com.medical.onepay.core.auth.application.ports.out.DgiiAuthPort;
import com.medical.onepay.core.auth.infrastructure.adapter.outbound.DgiiHttpClientAdapter;
import com.medical.onepay.core.auth.infrastructure.dto.DgiiTokenResponse;
import com.medical.onepay.core.common.infrastructure.crypto.XmlSignerAdapter;
import com.medical.onepay.core.features.digitalCertificates.domain.model.DigitalCertificateEntity;
import com.medical.onepay.core.features.digitalCertificates.domain.repository.DigitalCertificateRepository;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.medical.onepay.config.dgii.DgiiApiProperties;

@Service
@RequiredArgsConstructor
public class GetTokenDgiiUseCase implements DgiiAuthPort {

    private final DgiiApiProperties dgiiApiProperties;
    private final DigitalCertificateRepository digitalCertificatedRepository;
    private final XmlSignerAdapter xmlSignerAdapter;
    private final DgiiHttpClientAdapter dgiiHttpClientAdapter;

    @Override
    public DgiiTokenResponse obtenerToken(UUID tenantId) {
        try (HttpClient client = HttpClient.newHttpClient()) {

            // 0. Obtener certificado digital
            DigitalCertificateEntity certificate = digitalCertificatedRepository.findByTenantId(tenantId)
                    .orElseThrow(() -> new RuntimeException("No se encontró el certificado para el tenant: " + tenantId));

            if (certificate.getCertificateData() == null || certificate.getCertificateData().length == 0) {
                throw new RuntimeException("El certificado está vacío para este tenant: " + tenantId);
            }

            // 1. Obtener semilla
            URI urlSemilla = URI.create(dgiiApiProperties.getBaseUrl())
                    .resolve(dgiiApiProperties.getEndpoints().getAuth().getSeed());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(urlSemilla)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String semillaSinFirmar = response.body();

            // 2. Firmar la semilla
            String semillaFirmada = firmarSemilla(semillaSinFirmar, certificate.getCertificateData(), certificate.getPassword());

            // 3. Validar semilla
            String respuestaTokenXml = validarSemilla(semillaFirmada);

            // 4. Convertir JSON a DTO
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(respuestaTokenXml);

            JsonNode tokenNode = jsonNode.get("token");
            JsonNode expedidoNode = jsonNode.get("expedido");
            JsonNode expiraNode = jsonNode.get("expira");

            if (tokenNode == null || expedidoNode == null || expiraNode == null) {
                throw new RuntimeException("DGII no devolvió token válido. Respuesta completa: " + respuestaTokenXml);
            }

            return new DgiiTokenResponse(
                    tokenNode.asText(),
                    ZonedDateTime.parse(expedidoNode.asText()),
                    ZonedDateTime.parse(expiraNode.asText())
            );

        } catch (Exception e) {
            throw new RuntimeException("No se pudo obtener el token", e);
        }
    }

    private String firmarSemilla(String semillaXml, byte[] certBytes, String certPassword) {
        try (InputStream certStream = new ByteArrayInputStream(certBytes)) {
            return xmlSignerAdapter.sign(semillaXml, certStream, certPassword);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo firmar la semilla", e);
        }
    }

    private String validarSemilla(String semillaFirmadaXml) {
        File tempFile = null;
        try {
            // Guardar temporalmente el XML firmado
            tempFile = File.createTempFile("semilla-firmada-", ".xml");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(semillaFirmadaXml.getBytes(StandardCharsets.UTF_8));
            }

            String url = dgiiApiProperties.getBaseUrl() +
                    dgiiApiProperties.getEndpoints().getAuth().getValidate();

            return dgiiHttpClientAdapter.sendSignedXml(tempFile, url, null);

        } catch (Exception e) {
            throw new RuntimeException("No se pudo validar la semilla", e);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
