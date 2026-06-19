package com.unialfa.bolao.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "log_resultados")
public class LogResultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long partidaId;

    private Integer golsA;
    private Integer golsB;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registradoEm;

    @PrePersist
    public void prePersist() {
        registradoEm = LocalDateTime.now();
    }

    public LogResultado() {
    }

    public LogResultado(Long partidaId, Integer golsA, Integer golsB) {
        this.partidaId = partidaId;
        this.golsA = golsA;
        this.golsB = golsB;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPartidaId() { return partidaId; }
    public void setPartidaId(Long partidaId) { this.partidaId = partidaId; }
    public Integer getGolsA() { return golsA; }
    public void setGolsA(Integer golsA) { this.golsA = golsA; }
    public Integer getGolsB() { return golsB; }
    public void setGolsB(Integer golsB) { this.golsB = golsB; }
    public LocalDateTime getRegistradoEm() { return registradoEm; }
    public void setRegistradoEm(LocalDateTime registradoEm) { this.registradoEm = registradoEm; }
}
