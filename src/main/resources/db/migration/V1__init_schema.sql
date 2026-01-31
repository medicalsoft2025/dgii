CREATE TABLE tenants (
    id UUID PRIMARY KEY,
    tenant_code VARCHAR(50) NOT NULL UNIQUE,
    tipo_ecf VARCHAR(2),
    encf VARCHAR(20),
    fecha_vencimiento_secuencia DATE,
    indicador_monto_gravado INTEGER,
    tipo_ingresos VARCHAR(2),
    tipo_pago INTEGER,
    rnc_emisor VARCHAR(20),
    razon_social_emisor VARCHAR(255),
    nombre_comercial VARCHAR(255),
    direccion_emisor VARCHAR(255),
    municipio VARCHAR(10),
    provincia VARCHAR(10),
    correo_emisor VARCHAR(255),
    web_site VARCHAR(255),
    codigo_vendedor VARCHAR(60),
    numero_factura_interna VARCHAR(50),
    numero_pedido_interno VARCHAR(50),
    zona_venta VARCHAR(50),
    fecha_emision DATE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    tenant_id UUID
);

CREATE TABLE tenant_phones (
    id UUID PRIMARY KEY,
    tenant_id UUID REFERENCES tenants(id) ON DELETE CASCADE,
    telefono VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE digital_certificates (
    id UUID PRIMARY KEY,
    tenant_id UUID REFERENCES tenants(id) ON DELETE CASCADE,
    certificate_data BYTEA NOT NULL,
    password VARCHAR(255) NOT NULL,
    filename VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE invoice_audit (
    id UUID PRIMARY KEY,
    tenant_id UUID REFERENCES tenants(id),
    xml_content TEXT,
    encf VARCHAR(20),
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50),
    response_content TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE
);
