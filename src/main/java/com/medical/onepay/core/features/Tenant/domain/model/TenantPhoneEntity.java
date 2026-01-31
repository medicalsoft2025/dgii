package com.medical.onepay.core.features.Tenant.domain.model;

import com.medical.onepay.core.base.BaseModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tenant_phones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TenantPhoneEntity extends BaseModel {

    @Column(name = "telefono", length = 20)
    private String telefono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", referencedColumnName = "id", insertable = false, updatable = false)
    private TenantEntity tenant;
}
