package com.unialfa.bolao.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lista de tokens JWT revogados (logout — RF-005).
 * Como o JWT é stateless, mantemos em memória os tokens invalidados até a sua
 * expiração natural, permitindo barrar requisições com um token "deslogado".
 */
@Service
public class TokenDenylistService {

    private final Map<String, Instant> revogados = new ConcurrentHashMap<>();

    public void revogar(String token, Instant expiraEm) {
        if (token == null || token.isBlank()) {
            return;
        }
        limparExpirados();
        revogados.put(token, expiraEm != null ? expiraEm : Instant.now().plusSeconds(86400));
    }

    public boolean estaRevogado(String token) {
        Instant exp = revogados.get(token);
        if (exp == null) {
            return false;
        }
        if (exp.isBefore(Instant.now())) {
            revogados.remove(token);
            return false;
        }
        return true;
    }

    private void limparExpirados() {
        Instant agora = Instant.now();
        revogados.entrySet().removeIf(e -> e.getValue().isBefore(agora));
    }
}
