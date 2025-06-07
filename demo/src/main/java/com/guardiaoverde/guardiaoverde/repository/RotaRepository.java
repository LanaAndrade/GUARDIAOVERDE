package com.guardiaoverde.guardiaoverde.repository;

import com.guardiaoverde.guardiaoverde.domain.Rota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RotaRepository extends JpaRepository<Rota, Long> {

    /**
     * Busca rotas cujo ponto de partida contenha o trecho informado (ignora maiúsculas/minúsculas).
     */
    List<Rota> findByPontoPartidaContainingIgnoreCase(String trecho);

    /**
     * Busca rotas cujo ponto de destino contenha o trecho informado (ignora maiúsculas/minúsculas).
     */

    Optional<Rota> findByPontoDestinoContainingIgnoreCase(String pontoDestino);

}
