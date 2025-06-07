package com.guardiaoverde.guardiaoverde.repository;

import com.guardiaoverde.guardiaoverde.domain.PoliciaMilitar;
import com.guardiaoverde.guardiaoverde.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para a entidade PolíciaMilitar.
 */
@Repository
public interface PoliciaMilitarRepository extends JpaRepository<PoliciaMilitar, Long> {

    /**
     * Busca um policial militar por matrícula.
     */
    Optional<PoliciaMilitar> findByMatricula(String matricula);

    /**
     * Verifica se já existe um policial militar com determinada matrícula.
     */
    boolean existsByMatricula(String matricula);

    /**
     * Busca policiais cujo nome contenha o trecho informado (ignorando maiúsculas/minúsculas).
     */
    List<PoliciaMilitar> findByNomeContainingIgnoreCase(String trecho);

    /**
     * Busca todos os policiais vinculados a um determinado usuário (pelo ID do usuário).
     */
    Optional<PoliciaMilitar> findByUsuario(Usuario usuario);
}
