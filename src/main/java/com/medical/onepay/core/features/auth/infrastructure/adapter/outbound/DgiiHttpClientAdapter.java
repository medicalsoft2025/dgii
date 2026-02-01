package com.medical.onepay.core.features.auth.infrastructure.adapter.outbound;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import com.medical.onepay.core.features.auth.application.ports.out.DgiiSenderPort;

@Service
public class DgiiHttpClientAdapter implements DgiiSenderPort {

    public String sendSignedXml(File xmlFile, String url, String token) {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            HttpPost post = new HttpPost(url);

            FileBody fileBody = new FileBody(
                    xmlFile,
                    ContentType.APPLICATION_XML
            );

            HttpEntity entity = MultipartEntityBuilder.create()
                    .addPart("xml", fileBody)
                    .build();

            post.setEntity(entity);

            if (token != null && !token.isBlank()) {
                post.setHeader("Authorization", "Bearer " + token);
            }

            HttpResponse response = httpClient.execute(post);

            return new String(
                    response.getEntity().getContent().readAllBytes()
            );

        } catch (Exception e) {
            throw new RuntimeException("Error enviando XML a DGII", e);
        }
    }
}
