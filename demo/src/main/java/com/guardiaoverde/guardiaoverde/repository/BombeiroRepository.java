package com.guardiaoverde.guardiaoverde.repository;

import com.guardiaoverde.guardiaoverde.domain.Bombeiro;
import com.guardiaoverde.guardiaoverde.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório para a entidade Bombeiro.
 */
@Repository
public interface BombeiroRepository extends JpaRepository<Bombeiro, Long> {

    /**
     * Busca bombeiros cujo nome contenha o trecho informado (ignora maiúsculas/minúsculas).
     */
    List<Bombeiro> findByNomeContainingIgnoreCase(String trecho);

    /**
     * Busca bombeiros por turno de trabalho (ex.: "Manhã", "Noite").
     */
    List<Bombeiro> findByTurno(String turno);

    /**
     * Busca bombeiros vinculados a um determinado usuário.
     */
    List<Bombeiro> findByUsuario(Usuario usuario);

    List<Bombeiro> findAll();
}
