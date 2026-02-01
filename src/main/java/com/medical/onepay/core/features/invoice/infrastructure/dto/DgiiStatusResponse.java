package com.medical.onepay.core.features.invoice.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DgiiStatusResponse {
    private String trackId;
    private String codigo;
    private String estado;
    private String rnc;
    private String encf;
    private boolean secuenciaUtilizada;
    private String fechaRecepcion;
    private List<Mensaje> mensajes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Mensaje {
        private String valor;
        private int codigo;
    }
}
