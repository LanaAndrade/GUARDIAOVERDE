package com.guardiaoverde.guardiaoverde.repository;

import com.guardiaoverde.guardiaoverde.domain.Regiao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para a entidade Regiao.
 */
@Repository
public interface RegiaoRepository extends JpaRepository<Regiao, Long> {

    /**
     * Busca regiões cujo nome contenha o trecho informado (ignorando maiúsculas/minúsculas).
     */
    List<Regiao> findByNomeContainingIgnoreCase(String trecho);

    /**
     * Busca regiões pelo tipo de vegetação exata.
     */
    List<Regiao> findByTipoVegetacao(String tipoVegetacao);

    /**
     * Busca por índice de secura menor ou igual ao valor informado.
     */
    List<Regiao> findByIndiceSecuraLessThanEqual(Double valorMaximo);

    /**
     * Busca uma região exatamente pelo nome.
     */
    Optional<Regiao> findByNome(String nome);

    // Ou, se quiser buscar pela localizacao textual:
    Optional<Regiao> findByLimitesGeograficos(String limites);

}
