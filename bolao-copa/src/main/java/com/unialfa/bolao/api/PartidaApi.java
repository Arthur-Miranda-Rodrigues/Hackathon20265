package com.unialfa.bolao.api;

import com.unialfa.bolao.model.FasePartida;
import com.unialfa.bolao.model.Partida;
import com.unialfa.bolao.model.StatusPartida;
import com.unialfa.bolao.service.PartidaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/partidas")
public class PartidaApi {

    private final PartidaService service;

    public PartidaApi(PartidaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PartidaResponse>> listar(
            @RequestParam(required = false) FasePartida fase,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(required = false) StatusPartida status) {
        return ResponseEntity.ok(service.listar(fase, data, status).stream().map(PartidaResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartidaResponse> detalhe(@PathVariable Long id) {
        return ResponseEntity.ok(PartidaResponse.from(service.buscar(id)));
    }

    @GetMapping("/proximas")
    public ResponseEntity<List<PartidaResponse>> proximas(Authentication authentication) {
        return ResponseEntity.ok(service.proximasParaPalpitar(authentication.getName()).stream().map(PartidaResponse::from).toList());
    }

    public record PartidaResponse(
            Long id,
            String selecaoA,
            String selecaoB,
            String codigoFifaA,
            String codigoFifaB,
            Integer golsA,
            Integer golsB,
            LocalDateTime dataHora,
            String estadio,
            String fase,
            String grupo,
            String status
    ) {
        public static PartidaResponse from(Partida p) {
            return new PartidaResponse(
                    p.getId(),
                    p.getSelecaoA() != null ? p.getSelecaoA().getNome() : null,
                    p.getSelecaoB() != null ? p.getSelecaoB().getNome() : null,
                    p.getSelecaoA() != null ? p.getSelecaoA().getCodigoFifa() : null,
                    p.getSelecaoB() != null ? p.getSelecaoB().getCodigoFifa() : null,
                    p.getGolsA(),
                    p.getGolsB(),
                    p.getDataHora(),
                    p.getEstadio(),
                    p.getFase() != null ? p.getFase().name() : null,
                    p.getGrupo(),
                    p.getStatus() != null ? p.getStatus().name() : null
            );
        }
    }
}
