package com.medical.onepay.core.features.invoice.application.ports;

import com.medical.onepay.core.features.invoice.infrastructure.dto.DgiiStatusResponse;

public interface DgiiStatusPort {
    DgiiStatusResponse checkStatus(String trackId, String url, String token);
}
