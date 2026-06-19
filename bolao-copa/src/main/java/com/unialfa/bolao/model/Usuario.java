package com.unialfa.bolao.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuario_email", columnNames = "email")
})
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String senha;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilUsuario perfil = PerfilUsuario.USER;

    @Column(nullable = false)
    private Boolean bloqueado = false;

    @Column(nullable = false)
    private Integer pontuacaoTotal = 0;

    @Column(nullable = false)
    private Integer placaresExatos = 0;

    private LocalDateTime ultimoLoginEm;

    @JsonIgnore
    private String tokenRecuperacaoSenha;

    @JsonIgnore
    private LocalDateTime tokenRecuperacaoExpiraEm;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        criadoEm = agora;
        atualizadoEm = agora;
        if (bloqueado == null) bloqueado = false;
        if (perfil == null) perfil = PerfilUsuario.USER;
        if (pontuacaoTotal == null) pontuacaoTotal = 0;
        if (placaresExatos == null) placaresExatos = 0;
    }

    @PreUpdate
    public void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = perfil == PerfilUsuario.ADMIN ? "ROLE_ADMIN" : "ROLE_USER";
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return senha;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !Boolean.TRUE.equals(bloqueado);
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return !Boolean.TRUE.equals(bloqueado);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    @JsonIgnore
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public PerfilUsuario getPerfil() { return perfil; }
    public void setPerfil(PerfilUsuario perfil) { this.perfil = perfil; }
    public Boolean getBloqueado() { return bloqueado; }
    public void setBloqueado(Boolean bloqueado) { this.bloqueado = bloqueado; }
    public Integer getPontuacaoTotal() { return pontuacaoTotal; }
    public void setPontuacaoTotal(Integer pontuacaoTotal) { this.pontuacaoTotal = pontuacaoTotal; }
    public Integer getPlacaresExatos() { return placaresExatos; }
    public void setPlacaresExatos(Integer placaresExatos) { this.placaresExatos = placaresExatos; }
    public LocalDateTime getUltimoLoginEm() { return ultimoLoginEm; }
    public void setUltimoLoginEm(LocalDateTime ultimoLoginEm) { this.ultimoLoginEm = ultimoLoginEm; }
    public String getTokenRecuperacaoSenha() { return tokenRecuperacaoSenha; }
    public void setTokenRecuperacaoSenha(String tokenRecuperacaoSenha) { this.tokenRecuperacaoSenha = tokenRecuperacaoSenha; }
    public LocalDateTime getTokenRecuperacaoExpiraEm() { return tokenRecuperacaoExpiraEm; }
    public void setTokenRecuperacaoExpiraEm(LocalDateTime tokenRecuperacaoExpiraEm) { this.tokenRecuperacaoExpiraEm = tokenRecuperacaoExpiraEm; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
