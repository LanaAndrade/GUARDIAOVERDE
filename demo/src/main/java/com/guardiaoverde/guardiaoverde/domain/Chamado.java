package com.guardiaoverde.guardiaoverde.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chamados")
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Origem pode ser “USUARIO” ou “SISTEMA” (sensores automáticos)
    @Column(nullable = false, length = 20)
    private String origem;

    // Descrição da solicitação ou alerta
    @Column(nullable = false, length = 500)
    private String descricao;

    // Relacionamento com Regiao: a qual região o chamado pertence
    @ManyToOne
    @JoinColumn(name = "regiao_id", nullable = false)
    private Regiao regiao;

    // Momento em que o chamado foi registrado
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    // Prioridade do atendimento (e.g., “BAIXA”, “MÉDIA”, “ALTA”)
    @Column(nullable = false, length = 10)
    private String prioridade;

    public Chamado() {
    }

    public Chamado(String origem, String descricao, Regiao regiao,
                   LocalDateTime dataHora, String prioridade) {
        this.origem = origem;
        this.descricao = descricao;
        this.regiao = regiao;
        this.dataHora = dataHora;
        this.prioridade = prioridade;
    }

    public Long getId() {
        return id;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Regiao getRegiao() {
        return regiao;
    }

    public void setRegiao(Regiao regiao) {
        this.regiao = regiao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }
}
