package com.medical.onepay.core.features.Tenant.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    private Instant deletedAt;

    @Column(nullable = false)
    private Boolean active = true;

    // This entity does NOT have a @TenantId

    @Column(name = "tenant_code", nullable = false, unique = true, length = 50)
    private String tenantCode;

    // -------- IdDoc Fields --------
    @Column(name = "tipo_ecf", length = 2)
    private String tipoEcf;

    @Column(name = "encf", length = 20)
    private String encf;

    @Column(name = "fecha_vencimiento_secuencia")
    private LocalDate fechaVencimientoSecuencia;

    @Column(name = "indicador_monto_gravado")
    private Integer indicadorMontoGravado;

    @Column(name = "tipo_ingresos", length = 2)
    private String tipoIngresos;

    @Column(name = "tipo_pago")
    private Integer tipoPago;

    // -------- Emisor Fields --------
    @Column(name = "rnc_emisor", length = 20)
    private String rncEmisor;

    @Column(name = "razon_social_emisor", length = 255)
    private String razonSocialEmisor;

    @Column(name = "nombre_comercial", length = 255)
    private String nombreComercial;

    @Column(name = "direccion_emisor", length = 255)
    private String direccionEmisor;

    @Column(name = "municipio", length = 10)
    private String municipio;

    @Column(name = "provincia", length = 10)
    private String provincia;

    @Column(name = "correo_emisor", length = 255)
    private String correoEmisor;

    @Column(name = "web_site", length = 255)
    private String webSite;

    @Column(name = "codigo_vendedor", length = 60)
    private String codigoVendedor;

    @Column(name = "numero_factura_interna", length = 50)
    private String numeroFacturaInterna;

    @Column(name = "numero_pedido_interno", length = 50)
    private String numeroPedidoInterno;

    @Column(name = "zona_venta", length = 50)
    private String zonaVenta;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TenantPhoneEntity> phones = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }
}
