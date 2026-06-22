package com.unialfa.bolao.config;

import com.unialfa.bolao.service.TokenDenylistService;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Rejeita tokens que foram revogados via logout (RF-005).
 */
public class JwtDenylistValidator implements OAuth2TokenValidator<Jwt> {

    private final TokenDenylistService denylist;

    public JwtDenylistValidator(TokenDenylistService denylist) {
        this.denylist = denylist;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (denylist.estaRevogado(jwt.getTokenValue())) {
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Token revogado (logout).", null));
        }
        return OAuth2TokenValidatorResult.success();
    }
}
