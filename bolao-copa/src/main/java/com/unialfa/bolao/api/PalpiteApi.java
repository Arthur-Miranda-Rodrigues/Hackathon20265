package com.unialfa.bolao.api;

import com.unialfa.bolao.model.Palpite;
import com.unialfa.bolao.service.PalpiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/palpites")
public class PalpiteApi {

    private final PalpiteService service;

    public PalpiteApi(PalpiteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PalpiteResponse> registrar(Authentication authentication, @RequestBody PalpiteRequest request) {
        Palpite palpite = service.registrar(authentication.getName(), request.partidaId(), request.golsA(), request.golsB());
        return ResponseEntity.ok(PalpiteResponse.from(palpite));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PalpiteResponse> editar(Authentication authentication, @PathVariable Long id, @RequestBody PalpiteRequest request) {
        Palpite palpite = service.editar(authentication.getName(), id, request.golsA(), request.golsB());
        return ResponseEntity.ok(PalpiteResponse.from(palpite));
    }

    @GetMapping("/meus")
    public ResponseEntity<List<PalpiteResponse>> meus(Authentication authentication) {
        return ResponseEntity.ok(service.meus(authentication.getName()).stream().map(PalpiteResponse::from).toList());
    }

    public record PalpiteRequest(Long partidaId, Integer golsA, Integer golsB) {}

    public record PalpiteResponse(
            Long id,
            Long partidaId,
            String partida,
            LocalDateTime dataHora,
            Integer palpiteGolsA,
            Integer palpiteGolsB,
            Integer resultadoGolsA,
            Integer resultadoGolsB,
            Integer pontosObtidos,
            String criterioPontuacao
    ) {
        public static PalpiteResponse from(Palpite p) {
            String partidaNome = p.getPartida().getSelecaoA().getNome() + " x " + p.getPartida().getSelecaoB().getNome();
            return new PalpiteResponse(
                    p.getId(),
                    p.getPartida().getId(),
                    partidaNome,
                    p.getPartida().getDataHora(),
                    p.getGolsA(),
                    p.getGolsB(),
                    p.getPartida().getGolsA(),
                    p.getPartida().getGolsB(),
                    p.getPontosObtidos(),
                    p.getCriterioPontuacao() != null ? p.getCriterioPontuacao().name() : null
            );
        }
    }
}
