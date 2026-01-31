package com.medical.onepay.core.features.Tenant.infrastructure.inbound;

import com.medical.onepay.core.features.Tenant.application.dto.TenantRequestDTO;
import com.medical.onepay.core.features.Tenant.application.dto.TenantResponseDTO;
import com.medical.onepay.core.features.Tenant.application.mapper.TenantMapper;
import com.medical.onepay.core.features.Tenant.application.useCase.*;
import com.medical.onepay.core.features.Tenant.domain.model.TenantEntity;
import com.medical.onepay.shared.responses.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final CreateTenantUseCase createTenantUseCase;
    private final GetAllTenanUseCase getAllTenanUseCase;
    private final GetTenanByIdUseCase getTenanByIdUseCase;
    private final UpdateTenantUseCase updateTenantUseCase;
    private final DeleteTenantUseCase deleteTenantUseCase;
    private final TenantMapper tenantMapper;

    @GetMapping
    public ResponseEntity<?> findAll(@PageableDefault(size = 10) Pageable pageable) {
        Page<TenantEntity> tenantsPage = getAllTenanUseCase.execute(pageable);
        Page<TenantResponseDTO> responsePage = tenantsPage.map(tenantMapper::toResponse);
        return ResponseUtil.paged(responsePage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        TenantEntity tenant = getTenanByIdUseCase.execute(id);
        return ResponseUtil.ok(tenantMapper.toResponse(tenant));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody TenantRequestDTO request) {
        TenantEntity entity = tenantMapper.toEntity(request);
        TenantEntity saved = createTenantUseCase.execute(entity);
        return ResponseUtil.created(tenantMapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody TenantRequestDTO request) {
        // First retrieve existing to map updates
        TenantEntity existing = getTenanByIdUseCase.execute(id);
        tenantMapper.updateEntityFromDto(request, existing);
        
        TenantEntity updated = updateTenantUseCase.execute(id, existing);
        return ResponseUtil.ok(tenantMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        deleteTenantUseCase.execute(id);
        return ResponseUtil.noContent();
    }
}
