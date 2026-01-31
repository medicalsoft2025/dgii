package com.medical.onepay.core.features.digitalCertificates.application.mapper;

import com.medical.onepay.core.features.digitalCertificates.application.dto.DigitalCertificateResponseDTO;
import com.medical.onepay.core.features.digitalCertificates.domain.model.DigitalCertificateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DigitalCertificateMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "filename", target = "filename")
    @Mapping(source = "uploadedAt", target = "uploadedAt")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    DigitalCertificateResponseDTO toResponse(DigitalCertificateEntity entity);
}
