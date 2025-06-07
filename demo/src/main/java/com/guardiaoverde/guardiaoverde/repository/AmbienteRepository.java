package com.guardiaoverde.guardiaoverde.repository;

import com.guardiaoverde.guardiaoverde.domain.Ambiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório para a entidade Ambiente.
 */
@Repository
public interface AmbienteRepository extends JpaRepository<Ambiente, Long> {

    /**
     * Busca todos os ambientes cujo clima corresponda exatamente ao valor informado.
     */
    List<Ambiente> findByClima(String clima);

    /**
     * Busca todos os ambientes cuja localização contenha o trecho (case‐insensitive).
     */
    List<Ambiente> findByLocalizacaoContainingIgnoreCase(String trecho);

    /**
     * Busca todos os ambientes cuja temperatura esteja entre os valores mínimo e máximo.
     */
    List<Ambiente> findByTemperaturaBetween(Double minTemperatura, Double maxTemperatura);

    /**
     * Busca todos os ambientes cuja umidade esteja entre os valores mínimo e máximo.
     */
    List<Ambiente> findByUmidadeBetween(Double minUmidade, Double maxUmidade);

    List<Ambiente> findByTemperaturaLessThanAndUmidadeLessThan(Double temperaturaMax, Double umidadeMax);

}
