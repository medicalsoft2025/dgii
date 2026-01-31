package com.medical.onepay.config.filter;

import com.medical.onepay.core.features.Tenant.application.useCase.TenantResolver;
import com.medical.onepay.core.features.Tenant.domain.model.TenantEntity;
import com.medical.onepay.shared.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    private final TenantResolver tenantResolver;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String host = request.getServerName();

        try {
            // Resolve tenant and set it in the context
            TenantEntity tenant = tenantResolver.resolveByHost(host);
            if (tenant != null) {
                TenantContext.setTenantId(tenant.getId());
            }

            filterChain.doFilter(request, response);

        } finally {
            // Always clear the context after the request is processed
            TenantContext.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // We still need to resolve tenants for the tenants endpoint to check permissions,
        // but the StatementInspector will handle the SQL filtering.
        // For simplicity, we can keep this, but a more advanced setup might involve security checks here.
        return path.startsWith("/dgii/api/tenants");
    }
}
