package com.medical.onepay.core.features.invoice.infrastructure.adapter.outbound;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.onepay.core.features.invoice.application.ports.DgiiInvoicePort;
import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiFacturaResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

@Component
public class DgiiInvoiceHttpClientAdapter implements DgiiInvoicePort {

    private final ObjectMapper objectMapper;

    public DgiiInvoiceHttpClientAdapter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public DgiiFacturaResponse send(String xmlFirmado, String url, String token) {
        File tempFile = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            tempFile = File.createTempFile("factura-firmada-", ".xml");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(xmlFirmado.getBytes(StandardCharsets.UTF_8));
            }

            HttpPost post = new HttpPost(url);
            FileBody fileBody = new FileBody(tempFile, ContentType.APPLICATION_XML);
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addPart("xml", fileBody)
                    .build();
            post.setEntity(entity);

            if (token != null && !token.isBlank()) {
                post.setHeader("Authorization", "Bearer " + token);
            }

            HttpResponse response = httpClient.execute(post);
            String jsonResponse = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);

            System.out.println("DGII Response: " + jsonResponse);

            return objectMapper.readValue(jsonResponse, DgiiFacturaResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("Error enviando la factura XML a la DGII", e);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
