package com.unialfa.bolao.repository;

import com.unialfa.bolao.model.PerfilUsuario;
import com.unialfa.bolao.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Usuario> findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(String nome, String email);
    long countByUltimoLoginEmAfter(LocalDateTime dataHora);
    Page<Usuario> findByPerfilOrderByPontuacaoTotalDescPlacaresExatosDescCriadoEmAsc(PerfilUsuario perfil, Pageable pageable);
    List<Usuario> findByPerfilOrderByPontuacaoTotalDescPlacaresExatosDescCriadoEmAsc(PerfilUsuario perfil);
}
