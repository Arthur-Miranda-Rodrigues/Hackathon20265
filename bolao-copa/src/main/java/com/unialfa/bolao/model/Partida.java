package com.unialfa.bolao.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "partidas")
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "selecao_a_id")
    private Selecao selecaoA;

    @ManyToOne(optional = false)
    @JoinColumn(name = "selecao_b_id")
    private Selecao selecaoB;

    private Integer golsA;
    private Integer golsB;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private String estadio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FasePartida fase;

    private String grupo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPartida status = StatusPartida.AGENDADA;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        criadoEm = agora;
        atualizadoEm = agora;
        if (status == null) status = StatusPartida.AGENDADA;
    }

    @PreUpdate
    public void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Selecao getSelecaoA() { return selecaoA; }
    public void setSelecaoA(Selecao selecaoA) { this.selecaoA = selecaoA; }
    public Selecao getSelecaoB() { return selecaoB; }
    public void setSelecaoB(Selecao selecaoB) { this.selecaoB = selecaoB; }
    public Integer getGolsA() { return golsA; }
    public void setGolsA(Integer golsA) { this.golsA = golsA; }
    public Integer getGolsB() { return golsB; }
    public void setGolsB(Integer golsB) { this.golsB = golsB; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getEstadio() { return estadio; }
    public void setEstadio(String estadio) { this.estadio = estadio; }
    public FasePartida getFase() { return fase; }
    public void setFase(FasePartida fase) { this.fase = fase; }
    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }
    public StatusPartida getStatus() { return status; }
    public void setStatus(StatusPartida status) { this.status = status; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
