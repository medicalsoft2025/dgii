package com.medical.onepay.core.features.invoice.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DgiiFacturaResponse {
    private String trackId;

    @JsonProperty("error")
    private String estado;

    @JsonProperty("mensaje")
    private String mensajes;
}
