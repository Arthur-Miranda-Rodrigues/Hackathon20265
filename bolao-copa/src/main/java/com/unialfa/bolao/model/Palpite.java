package com.unialfa.bolao.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "palpites", uniqueConstraints = {
        @UniqueConstraint(name = "uk_palpite_usuario_partida", columnNames = {"usuario_id", "partida_id"})
})
public class Palpite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "partida_id")
    private Partida partida;

    @Column(nullable = false)
    private Integer golsA;

    @Column(nullable = false)
    private Integer golsB;

    @Column(nullable = false)
    private Integer pontosObtidos = 0;

    @Enumerated(EnumType.STRING)
    private CriterioPontuacao criterioPontuacao;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        criadoEm = agora;
        atualizadoEm = agora;
        if (pontosObtidos == null) pontosObtidos = 0;
    }

    @PreUpdate
    public void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Partida getPartida() { return partida; }
    public void setPartida(Partida partida) { this.partida = partida; }
    public Integer getGolsA() { return golsA; }
    public void setGolsA(Integer golsA) { this.golsA = golsA; }
    public Integer getGolsB() { return golsB; }
    public void setGolsB(Integer golsB) { this.golsB = golsB; }
    public Integer getPontosObtidos() { return pontosObtidos; }
    public void setPontosObtidos(Integer pontosObtidos) { this.pontosObtidos = pontosObtidos; }
    public CriterioPontuacao getCriterioPontuacao() { return criterioPontuacao; }
    public void setCriterioPontuacao(CriterioPontuacao criterioPontuacao) { this.criterioPontuacao = criterioPontuacao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
