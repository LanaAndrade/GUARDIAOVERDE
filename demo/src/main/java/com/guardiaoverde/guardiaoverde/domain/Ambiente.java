package com.guardiaoverde.guardiaoverde.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "ambientes")
public class Ambiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String clima;

    @Column(nullable = false)
    private Double temperatura;

    @Column(nullable = false)
    private Double umidade;

    @Column(nullable = false, length = 100)
    private String localizacao;

    public Ambiente() {
    }

    public Ambiente(String clima, Double temperatura, Double umidade, String localizacao) {
        this.clima = clima;
        this.temperatura = temperatura;
        this.umidade = umidade;
        this.localizacao = localizacao;
    }

    public Long getId() {
        return id;
    }

    public String getClima() {
        return clima;
    }

    public void setClima(String clima) {
        this.clima = clima;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Double getUmidade() {
        return umidade;
    }

    public void setUmidade(Double umidade) {
        this.umidade = umidade;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }
}
