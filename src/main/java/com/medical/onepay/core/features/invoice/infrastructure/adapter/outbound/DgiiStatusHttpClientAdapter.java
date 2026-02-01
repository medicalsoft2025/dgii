package com.medical.onepay.core.features.invoice.infrastructure.adapter.outbound;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.onepay.core.features.invoice.application.ports.DgiiStatusPort;
import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiStatusResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class DgiiStatusHttpClientAdapter implements DgiiStatusPort {

    private final ObjectMapper objectMapper;

    public DgiiStatusHttpClientAdapter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public DgiiStatusResponse checkStatus(String trackId, String url, String token) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            
            String fullUrl = url + "?trackId=" + trackId;
            HttpGet get = new HttpGet(fullUrl);

            if (token != null && !token.isBlank()) {
                get.setHeader("Authorization", "Bearer " + token);
            }

            HttpResponse response = httpClient.execute(get);
            String jsonResponse = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);

            return objectMapper.readValue(jsonResponse, DgiiStatusResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("Error consultando el estado del trackId: " + trackId, e);
        }
    }
}
