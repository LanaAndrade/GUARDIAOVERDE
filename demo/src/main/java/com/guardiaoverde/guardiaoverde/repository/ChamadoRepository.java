package com.guardiaoverde.guardiaoverde.repository;

import com.guardiaoverde.guardiaoverde.domain.Chamado;
import com.guardiaoverde.guardiaoverde.domain.Regiao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório para a entidade Chamado.
 */
@Repository
public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    /**
     * Retorna todos os chamados de uma dada origem (ex.: "USUARIO" ou "SISTEMA").
     */
    List<Chamado> findByOrigem(String origem);

    /**
     * Retorna todos os chamados de uma dada prioridade (ex.: "BAIXA", "MÉDIA", "ALTA").
     */
    List<Chamado> findByPrioridade(String prioridade);

    /**
     * Busca chamados cujo texto da descrição contenha o trecho informado (ignorando maiúsculas/minúsculas).
     */
    List<Chamado> findByDescricaoContainingIgnoreCase(String trecho);

    /**
     * Retorna todos os chamados pertencentes à região de ID informado.
     */
    List<Chamado> findByRegiaoId(Long regiaoId);

    List<Chamado> findTopByRegiaoOrderByDataHoraDesc(Regiao r);

    List<Chamado> findByRegiao(Regiao r);
}
