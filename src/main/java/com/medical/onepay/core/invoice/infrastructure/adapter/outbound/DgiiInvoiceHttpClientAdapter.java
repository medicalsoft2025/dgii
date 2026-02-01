package com.medical.onepay.core.invoice.infrastructure.adapter.outbound;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

@Service
public class DgiiInvoiceHttpClientAdapter {

    /**
     * Envía una factura firmada en formato XML a la DGII.
     *
     * @param xmlFirmado El contenido del XML de la factura, ya firmado.
     * @param url        La URL del endpoint de recepción de facturas de la DGII.
     * @param token      El token de autenticación Bearer.
     * @return La respuesta del servidor de la DGII.
     */
    public String send(String xmlFirmado, String url, String token) {
        File tempFile = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 1. Crear un archivo temporal para el XML firmado
            tempFile = File.createTempFile("factura-firmada-", ".xml");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(xmlFirmado.getBytes(StandardCharsets.UTF_8));
            }

            // 2. Construir la petición POST
            HttpPost post = new HttpPost(url);

            // 3. Crear el cuerpo del archivo para la petición multipart
            FileBody fileBody = new FileBody(tempFile, ContentType.APPLICATION_XML);

            // 4. Construir la entidad multipart
            // La DGII espera que el archivo XML venga en un campo llamado "xml"
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addPart("xml", fileBody)
                    .build();

            post.setEntity(entity);

            // 5. Añadir el header de autorización si el token existe
            if (token != null && !token.isBlank()) {
                post.setHeader("Authorization", "Bearer " + token);
            }

            // 6. Ejecutar la petición y obtener la respuesta
            HttpResponse response = httpClient.execute(post);

            return new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Error enviando la factura XML a la DGII", e);
        } finally {
            // 7. Asegurarse de eliminar el archivo temporal
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
