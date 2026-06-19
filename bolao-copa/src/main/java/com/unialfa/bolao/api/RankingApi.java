package com.unialfa.bolao.api;

import com.unialfa.bolao.model.Usuario;
import com.unialfa.bolao.service.RankingService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ranking")
public class RankingApi {

    private final RankingService service;

    public RankingApi(RankingService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<RankingResponse>> ranking(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(service.ranking(page, size).map(RankingResponse::from));
    }

    @GetMapping("/me")
    public ResponseEntity<RankingService.PosicaoUsuario> minhaPosicao(Authentication authentication) {
        return ResponseEntity.ok(service.posicaoUsuario(authentication.getName()));
    }

    public record RankingResponse(Long id, String nome, Integer pontuacaoTotal, Integer placaresExatos) {
        public static RankingResponse from(Usuario usuario) {
            return new RankingResponse(usuario.getId(), usuario.getNome(), usuario.getPontuacaoTotal(), usuario.getPlacaresExatos());
        }
    }
}
