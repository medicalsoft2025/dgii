package com.medical.onepay.core.auth.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class DgiiTokenResponse {
    private String token;
    private ZonedDateTime expedido;
    private ZonedDateTime expira;
}