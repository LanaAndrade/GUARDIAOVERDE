package com.guardiaoverde.guardiaoverde.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quando o alerta foi gerado / recebido
    @Column(nullable = false)
    private LocalDateTime dataHora;

    // Nível de risco estimado (por exemplo: BAIXO, MÉDIO, ALTO)
    @Column(nullable = false, length = 20)
    private String nivelRisco;

    // Indica se há risco real de queimada (true = risco detectado)
    @Column(nullable = false)
    private Boolean riscoConfirmado;

    // Relaciona o alerta a um ambiente específico
    @ManyToOne
    @JoinColumn(name = "ambiente_id")
    private Ambiente ambiente;

    // (Opcional) Quem criou ou recebeu o alerta — pode ser Usuário, Polícia ou Bombeiro
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario responsavel;

    public Alerta() {
    }

    public Alerta(LocalDateTime dataHora,
                  String nivelRisco,
                  Boolean riscoConfirmado,
                  Ambiente ambiente,
                  Usuario responsavel) {
        this.dataHora = dataHora;
        this.nivelRisco = nivelRisco;
        this.riscoConfirmado = riscoConfirmado;
        this.ambiente = ambiente;
        this.responsavel = responsavel;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getNivelRisco() {
        return nivelRisco;
    }

    public void setNivelRisco(String nivelRisco) {
        this.nivelRisco = nivelRisco;
    }

    public Boolean getRiscoConfirmado() {
        return riscoConfirmado;
    }

    public void setRiscoConfirmado(Boolean riscoConfirmado) {
        this.riscoConfirmado = riscoConfirmado;
    }

    public Ambiente getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(Ambiente ambiente) {
        this.ambiente = ambiente;
    }

    public Usuario getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Usuario responsavel) {
        this.responsavel = responsavel;
    }
}
