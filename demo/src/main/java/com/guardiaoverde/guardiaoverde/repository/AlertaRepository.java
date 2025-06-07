package com.guardiaoverde.guardiaoverde.repository;

import com.guardiaoverde.guardiaoverde.domain.Alerta;
import com.guardiaoverde.guardiaoverde.domain.Ambiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para a entidade Alerta.
 */
@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    // Busca um alerta pelo nível de risco exato (e.g. "BAIXO", "MÉDIO", "ALTO")
    List<Alerta> findByNivelRisco(String nivelRisco);

    // Busca todos os alertas com risco confirmado ou não
    List<Alerta> findByRiscoConfirmado(Boolean riscoConfirmado);

    // Busca alertas gerados em um intervalo de datas
    List<Alerta> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    // Busca alertas de um determinado ambiente (id do Ambiente)
    List<Alerta> findByAmbienteId(Long ambienteId);

    // Busca alertas atribuídos a um determinado responsável (id do Usuário)
    List<Alerta> findByResponsavelId(Long responsavelId);

    // (Opcional) Retorna um alerta pelo ID e situação de confirmação (caso queira checar se existe e está confirmado)
    Optional<Alerta> findByIdAndRiscoConfirmado(Long id, Boolean riscoConfirmado);

    List<Alerta> findTopByAmbienteOrderByDataHoraDesc(Ambiente ambiente);

    List<Alerta> findByAmbiente(Ambiente ambiente);
}
