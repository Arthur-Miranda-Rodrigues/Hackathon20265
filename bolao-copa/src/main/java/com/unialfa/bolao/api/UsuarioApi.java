package com.unialfa.bolao.api;

import com.unialfa.bolao.model.Usuario;
import com.unialfa.bolao.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioApi {

    private final UsuarioService usuarioService;

    public UsuarioApi(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> editar(Authentication authentication, @RequestBody EditarPerfilRequest request) {
        Usuario usuario = usuarioService.editarPerfil(authentication.getName(), request.nome(), request.avatarUrl());
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Map<String, String>> excluir(Authentication authentication, @RequestBody ExcluirContaRequest request) {
        usuarioService.solicitarExclusaoConta(authentication.getName(), request.confirmacao());
        return ResponseEntity.ok(Map.of("mensagem", "Conta excluída com sucesso."));
    }

    public record EditarPerfilRequest(String nome, String avatarUrl) {}
    public record ExcluirContaRequest(Boolean confirmacao) {}

    public record UsuarioResponse(Long id, String nome, String email, String avatarUrl, String perfil, Boolean bloqueado, Integer pontuacaoTotal, Integer placaresExatos) {
        public static UsuarioResponse from(Usuario usuario) {
            return new UsuarioResponse(
                    usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getAvatarUrl(),
                    usuario.getPerfil().name(), usuario.getBloqueado(), usuario.getPontuacaoTotal(), usuario.getPlacaresExatos());
        }
    }
}
