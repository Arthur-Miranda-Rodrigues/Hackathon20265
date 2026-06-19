package com.unialfa.bolao.service;

import com.unialfa.bolao.exception.BusinessException;
import com.unialfa.bolao.exception.NotFoundException;
import com.unialfa.bolao.model.PerfilUsuario;
import com.unialfa.bolao.model.Usuario;
import com.unialfa.bolao.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }

    @Transactional
    public Usuario cadastrarUsuarioComum(String nome, String email, String senha) {
        validarTexto(nome, "Nome é obrigatório.");
        validarTexto(email, "E-mail é obrigatório.");
        validarTexto(senha, "Senha é obrigatória.");

        if (repository.existsByEmail(email)) {
            throw new BusinessException("Já existe um usuário cadastrado com este e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setPerfil(PerfilUsuario.USER);
        usuario.setBloqueado(false);
        return repository.save(usuario);
    }

    @Transactional
    public Usuario salvarAdmin(Usuario usuario) {
        validarTexto(usuario.getNome(), "Nome é obrigatório.");
        validarTexto(usuario.getEmail(), "E-mail é obrigatório.");

        if (usuario.getId() == null) {
            if (repository.existsByEmail(usuario.getEmail())) {
                throw new BusinessException("Já existe um usuário com este e-mail.");
            }
            validarTexto(usuario.getSenha(), "Senha é obrigatória.");
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            if (usuario.getPerfil() == null) usuario.setPerfil(PerfilUsuario.USER);
            if (usuario.getBloqueado() == null) usuario.setBloqueado(false);
            return repository.save(usuario);
        }

        Usuario atual = buscar(usuario.getId());
        if (!atual.getEmail().equals(usuario.getEmail()) && repository.existsByEmail(usuario.getEmail())) {
            throw new BusinessException("Já existe um usuário com este e-mail.");
        }

        atual.setNome(usuario.getNome());
        atual.setEmail(usuario.getEmail());
        atual.setAvatarUrl(usuario.getAvatarUrl());
        atual.setPerfil(usuario.getPerfil() == null ? PerfilUsuario.USER : usuario.getPerfil());
        atual.setBloqueado(Boolean.TRUE.equals(usuario.getBloqueado()));

        if (usuario.getSenha() != null && !usuario.getSenha().isBlank()) {
            atual.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }

        return repository.save(atual);
    }

    public Usuario buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
    }

    public Usuario buscarPorEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
    }

    public List<Usuario> listar() {
        return repository.findAll();
    }

    @Transactional
    public Usuario editarPerfil(String email, String nome, String avatarUrl) {
        Usuario usuario = buscarPorEmail(email);
        validarTexto(nome, "Nome é obrigatório.");
        usuario.setNome(nome);
        usuario.setAvatarUrl(avatarUrl);
        return repository.save(usuario);
    }

    @Transactional
    public void solicitarExclusaoConta(String email, Boolean confirmacao) {
        if (!Boolean.TRUE.equals(confirmacao)) {
            throw new BusinessException("Confirmação explícita obrigatória para excluir a conta.");
        }
        Usuario usuario = buscarPorEmail(email);
        repository.delete(usuario);
    }

    @Transactional
    public String solicitarRecuperacaoSenha(String email) {
        Usuario usuario = buscarPorEmail(email);
        String token = UUID.randomUUID().toString();
        usuario.setTokenRecuperacaoSenha(token);
        usuario.setTokenRecuperacaoExpiraEm(LocalDateTime.now().plusHours(2));
        repository.save(usuario);
        return token;
    }

    @Transactional
    public void registrarLogin(String email) {
        repository.findByEmail(email).ifPresent(usuario -> {
            usuario.setUltimoLoginEm(LocalDateTime.now());
            repository.save(usuario);
        });
    }

    @Transactional
    public void bloquear(Long id) {
        Usuario usuario = buscar(id);
        usuario.setBloqueado(true);
        repository.save(usuario);
    }

    @Transactional
    public void desbloquear(Long id) {
        Usuario usuario = buscar(id);
        usuario.setBloqueado(false);
        repository.save(usuario);
    }

    public long totalUsuarios() {
        return repository.count();
    }

    public long usuariosAtivosUltimas24h() {
        return repository.countByUltimoLoginEmAfter(LocalDateTime.now().minusHours(24));
    }

    private void validarTexto(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException(mensagem);
        }
    }
}
