package com.guardiaoverde.guardiaoverde.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "policias_militares")
public class PoliciaMilitar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Se cada policial for um usuário do sistema
    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 20)
    private String matricula;

    @Column(nullable = true, length = 20)
    private String telefone;

    public PoliciaMilitar() {
    }

    public PoliciaMilitar(Usuario usuario, String nome, String matricula, String telefone) {
        this.usuario = usuario;
        this.nome = nome;
        this.matricula = matricula;
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

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
