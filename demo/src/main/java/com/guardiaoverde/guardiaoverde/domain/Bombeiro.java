package com.guardiaoverde.guardiaoverde.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "bombeiros")
public class Bombeiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 50)
    private String turno;

    @Column(length = 20)
    private String telefone;

    public Bombeiro() {
    }

    public Bombeiro(Usuario usuario, String nome, String turno, String telefone) {
        this.usuario = usuario;
        this.nome = nome;
        this.turno = turno;
        this.telefone = telefone;
    }

    public Long getId() {
        return id;
    }

    // Adicionado para os testes
    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
