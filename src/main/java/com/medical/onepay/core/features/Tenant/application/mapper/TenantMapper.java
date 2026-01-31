package com.medical.onepay.core.features.Tenant.application.mapper;

import com.medical.onepay.core.features.Tenant.application.dto.TenantRequestDTO;
import com.medical.onepay.core.features.Tenant.application.dto.TenantResponseDTO;
import com.medical.onepay.core.features.Tenant.domain.model.TenantEntity;
import com.medical.onepay.core.features.Tenant.domain.model.TenantPhoneEntity;
import org.mapstruct.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Instant.class},
        builder = @Builder(disableBuilder = true)
)
public interface TenantMapper {

    // -------- CREATE --------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "phones", source = "dto.phones")
    TenantEntity toEntity(TenantRequestDTO dto);

    // -------- UPDATE --------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "phones", source = "dto.phones")
    void updateEntityFromDto(
            TenantRequestDTO dto,
            @MappingTarget TenantEntity entity
    );

    // -------- RESPONSE --------
    @Mapping(target = "phones", source = "entity.phones")
    TenantResponseDTO toResponse(TenantEntity entity);

    // Custom mapping for a single phone string to TenantPhoneEntity
    default TenantPhoneEntity map(String phoneString) {
        if (phoneString == null) {
            return null;
        }
        TenantPhoneEntity phoneEntity = new TenantPhoneEntity();
        phoneEntity.setTelefono(phoneString);
        return phoneEntity;
    }

    // Custom mapping for a single TenantPhoneEntity to phone string
    default String map(TenantPhoneEntity phoneEntity) {
        if (phoneEntity == null) {
            return null;
        }
        return phoneEntity.getTelefono();
    }

    // After mapping for toEntity to set the back-reference
    @AfterMapping
    default void setTenantForPhones(@MappingTarget TenantEntity tenantEntity) {
        if (tenantEntity.getPhones() != null) {
            for (TenantPhoneEntity phone : tenantEntity.getPhones()) {
                phone.setTenant(tenantEntity);
            }
        }
    }

    // After mapping for updateEntityFromDto to handle collection updates and set back-reference
    @AfterMapping
    default void updateTenantPhones(@MappingTarget TenantEntity tenantEntity, TenantRequestDTO dto) {
        if (dto.getPhones() == null || dto.getPhones().isEmpty()) {
            if (tenantEntity.getPhones() != null) {
                tenantEntity.getPhones().clear(); // Clear all existing phones if DTO list is null or empty
            }
            return;
        }

        // Create a temporary list of new phone entities from the DTO
        List<TenantPhoneEntity> newPhoneEntities = dto.getPhones().stream()
                .map(phoneString -> {
                    TenantPhoneEntity phoneEntity = new TenantPhoneEntity();
                    phoneEntity.setTelefono(phoneString);
                    phoneEntity.setTenant(tenantEntity); // Set back-reference
                    return phoneEntity;
                })
                .collect(Collectors.toList());

        // Update the existing collection in the target entity
        if (tenantEntity.getPhones() == null) {
            tenantEntity.setPhones(new ArrayList<>());
        } else {
            tenantEntity.getPhones().clear(); // Clear existing phones
        }
        tenantEntity.getPhones().addAll(newPhoneEntities); // Add new ones
    }
}
