package com.unialfa.bolao.api;

import com.unialfa.bolao.model.Usuario;
import com.unialfa.bolao.service.TokenService;
import com.unialfa.bolao.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthApi {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    public AuthApi(AuthenticationManager authenticationManager, TokenService tokenService, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponse> cadastro(@RequestBody CadastroRequest request) {
        Usuario usuario = usuarioService.cadastrarUsuarioComum(request.nome(), request.email(), request.senha());
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
        usuarioService.registrarLogin(request.email());
        return ResponseEntity.ok(Map.of("token", tokenService.gerar(auth)));
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<Map<String, String>> recuperarSenha(@RequestBody RecuperarSenhaRequest request) {
        usuarioService.solicitarRecuperacaoSenha(request.email());
        return ResponseEntity.ok(Map.of(
                "mensagem", "Se o e-mail estiver cadastrado, enviaremos as instruções de recuperação."
        ));
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Map<String, String>> redefinirSenha(@RequestBody RedefinirSenhaRequest request) {
        // Altere o método do seu usuarioService se o nome dos parâmetros for diferente
        usuarioService.alterarSenhaComToken(request.email(), request.token(), request.senha());

        return ResponseEntity.ok(Map.of("mensagem", "Senha alterada com sucesso!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("mensagem", "Sessão encerrada no dispositivo."));
    }

    public record CadastroRequest(String nome, String email, String senha) {}
    public record LoginRequest(String email, String senha) {}
    public record RecuperarSenhaRequest(String email) {}
    public record RedefinirSenhaRequest(String email, String token, String senha) {}

    public record UsuarioResponse(Long id, String nome, String email, String avatarUrl, String perfil) {
        public static UsuarioResponse from(Usuario usuario) {
            return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getAvatarUrl(), usuario.getPerfil().name());
        }
    }
}
