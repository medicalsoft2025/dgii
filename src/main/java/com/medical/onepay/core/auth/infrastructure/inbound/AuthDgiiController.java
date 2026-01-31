package com.medical.onepay.core.auth.infrastructure.inbound;

import com.medical.onepay.core.auth.application.usecase.GetTokenDgiiUseCase;
import com.medical.onepay.core.auth.infrastructure.dto.DgiiTokenResponse;
import com.medical.onepay.shared.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthDgiiController {

    private final GetTokenDgiiUseCase getTokenDgiiUseCase;

    @GetMapping("token")
    public ResponseEntity<ApiResponse<DgiiTokenResponse>> obtenerToken(@RequestParam("tenantId") UUID tenantId) {
        DgiiTokenResponse token = getTokenDgiiUseCase.obtenerToken(tenantId);
        ApiResponse<DgiiTokenResponse> response = new ApiResponse<>(
                200,
                "Token generado correctamente",
                token
        );
        return ResponseEntity.ok(response);
    }
}
