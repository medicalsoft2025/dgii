package com.medical.onepay.core.features.Tenant.application.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantRequestDTO {

    private String tenantCode;

    // IdDoc
    private String tipoEcf;
    private String encf;
    private LocalDate fechaVencimientoSecuencia;
    private Integer indicadorMontoGravado;
    private String tipoIngresos;
    private Integer tipoPago;

    // Emisor
    private String rncEmisor;
    private String razonSocialEmisor;
    private String nombreComercial;
    private String direccionEmisor;
    private String municipio;
    private String provincia;
    private String correoEmisor;
    private String webSite;
    private String codigoVendedor;
    private String numeroFacturaInterna;
    private String numeroPedidoInterno;
    private String zonaVenta;
    private LocalDate fechaEmision;

    private List<String> phones;
}
