package com.guardiaoverde.guardiaoverde.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "rotas")
public class Rota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ponto de partida (coordenada, nome ou referência textual)
    @Column(name = "ponto_partida", nullable = false, length = 100)
    private String pontoPartida;

    // Ponto de destino (coordenada, nome ou referência textual)
    @Column(name = "ponto_destino", nullable = false, length = 100)
    private String pontoDestino;

    // Tempo estimado de deslocamento (em minutos, por exemplo)
    @Column(name = "tempo_estimado", nullable = false)
    private Double tempoEstimado;

    // Distância total (em quilômetros, por exemplo)
    @Column(nullable = false)
    private Double distancia;

    // Armazena caminhos alternativos como CLOB (texto longo)
    @Lob
    @Column(name = "caminhos_alternativos")
    private String caminhosAlternativos;

    public Rota() {
    }

    public Rota(
            String pontoPartida,
            String pontoDestino,
            Double tempoEstimado,
            Double distancia,
            String caminhosAlternativos
    ) {
        this.pontoPartida = pontoPartida;
        this.pontoDestino = pontoDestino;
        this.tempoEstimado = tempoEstimado;
        this.distancia = distancia;
        this.caminhosAlternativos = caminhosAlternativos;
    }

    public Long getId() {
        return id;
    }

    public String getPontoPartida() {
        return pontoPartida;
    }

    public void setPontoPartida(String pontoPartida) {
        this.pontoPartida = pontoPartida;
    }

    public String getPontoDestino() {
        return pontoDestino;
    }

    public void setPontoDestino(String pontoDestino) {
        this.pontoDestino = pontoDestino;
    }

    public Double getTempoEstimado() {
        return tempoEstimado;
    }

    public void setTempoEstimado(Double tempoEstimado) {
        this.tempoEstimado = tempoEstimado;
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }

    public String getCaminhosAlternativos() {
        return caminhosAlternativos;
    }

    public void setCaminhosAlternativos(String caminhosAlternativos) {
        this.caminhosAlternativos = caminhosAlternativos;
    }
}
