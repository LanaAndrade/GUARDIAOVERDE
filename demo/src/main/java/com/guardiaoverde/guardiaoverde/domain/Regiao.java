package com.guardiaoverde.guardiaoverde.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "regioes")
public class Regiao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome da região de monitoramento
    @Column(nullable = false, length = 100)
    private String nome;

    // Limites geográficos como CLOB (texto longo)
    @Lob
    @Column(name = "limites_geograficos", nullable = false)
    private String limitesGeograficos;

    // Tipo de vegetação predominante (ex.: “Floresta”, “Cerrado”, “Caatinga”)
    @Column(name = "tipo_vegetacao", nullable = false, length = 50)
    private String tipoVegetacao;

    // Índice de secura (0.0 a 1.0 ou outra escala definida)
    @Column(name = "indice_secura", nullable = false)
    private Double indiceSecura;

    public Regiao() {
    }

    public Regiao(
            String nome,
            String limitesGeograficos,
            String tipoVegetacao,
            Double indiceSecura
    ) {
        this.nome = nome;
        this.limitesGeograficos = limitesGeograficos;
        this.tipoVegetacao = tipoVegetacao;
        this.indiceSecura = indiceSecura;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLimitesGeograficos() {
        return limitesGeograficos;
    }

    public void setLimitesGeograficos(String limitesGeograficos) {
        this.limitesGeograficos = limitesGeograficos;
    }

    public String getTipoVegetacao() {
        return tipoVegetacao;
    }

    public void setTipoVegetacao(String tipoVegetacao) {
        this.tipoVegetacao = tipoVegetacao;
    }

    public Double getIndiceSecura() {
        return indiceSecura;
    }

    public void setIndiceSecura(Double indiceSecura) {
        this.indiceSecura = indiceSecura;
    }
}
