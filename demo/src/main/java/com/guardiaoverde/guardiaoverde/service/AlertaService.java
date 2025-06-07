package com.guardiaoverde.guardiaoverde.service;

import com.guardiaoverde.guardiaoverde.domain.*;
import com.guardiaoverde.guardiaoverde.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final AmbienteRepository ambienteRepository;
    private final BombeiroRepository bombeiroRepository;
    private final PoliciaMilitarRepository policiaMilitarRepository;

    @Autowired
    public AlertaService(AlertaRepository alertaRepository,
                         AmbienteRepository ambienteRepository,
                         BombeiroRepository bombeiroRepository,
                         PoliciaMilitarRepository policiaMilitarRepository) {
        this.alertaRepository = alertaRepository;
        this.ambienteRepository = ambienteRepository;
        this.bombeiroRepository = bombeiroRepository;
        this.policiaMilitarRepository = policiaMilitarRepository;
    }

    /**
     * Retorna todos os alertas existentes.
     */
    public List<Alerta> listarTodos() {
        return alertaRepository.findAll();
    }

    /**
     * Retorna um alerta por ID, se existir.
     */
    public Optional<Alerta> buscarPorId(Long id) {
        return alertaRepository.findById(id);
    }

    /**
     * Cria um novo Alerta para um Ambiente específico.
     * Aplica validações de nível de risco, intervalo mínimo entre alertas e atribuição de responsável.
     */
    @Transactional
    public Alerta criarAlerta(Alerta novoAlerta) {
        // 1) Certificar que o ambiente existe
        Ambiente ambiente = ambienteRepository.findById(novoAlerta.getAmbiente().getId())
                .orElseThrow(() -> new IllegalArgumentException("Ambiente não encontrado"));
        novoAlerta.setAmbiente(ambiente);

        // 2) Se riscoConfirmado == true, nívelRisco não pode ser “BAIXO”
        if (Boolean.TRUE.equals(novoAlerta.getRiscoConfirmado())
                && "BAIXO".equalsIgnoreCase(novoAlerta.getNivelRisco())) {
            throw new IllegalArgumentException("Alerta confirmado não pode ter nível de risco BAIXO.");
        }

        // 3) Verificar intervalo mínimo de 5 minutos desde último alerta do mesmo nível no mesmo ambiente
        List<Alerta> ultimos = alertaRepository.findTopByAmbienteOrderByDataHoraDesc(ambiente);
        if (!ultimos.isEmpty()) {
            Alerta ultimo = ultimos.get(0);
            Duration diff = Duration.between(ultimo.getDataHora(), novoAlerta.getDataHora());
            if (diff.toMinutes() < 5
                    && ultimo.getNivelRisco().equalsIgnoreCase(novoAlerta.getNivelRisco())) {
                throw new IllegalArgumentException(
                        "Já existe alerta do mesmo nível neste ambiente há menos de 5 minutos.");
            }
        }

        // 4) Se riscoConfirmado for true, atribui automaticamente um bombeiro disponível
        if (Boolean.TRUE.equals(novoAlerta.getRiscoConfirmado())) {
            Bombeiro responsavel = buscarBombeiroDisponivelParaAmbiente(ambiente);
            if (responsavel != null) {
                novoAlerta.setResponsavel(responsavel.getUsuario());
            }
        }

        return alertaRepository.save(novoAlerta);
    }

    /**
     * Atualiza um Alerta existente (substituição total).
     * Se não for encontrado, lança IllegalArgumentException.
     */
    @Transactional
    public Alerta atualizarAlerta(Long id, Alerta alertaAtualizado) {
        return alertaRepository.findById(id).map(existing -> {
            // 1) Certificar que o ambiente existe
            Ambiente ambiente = ambienteRepository.findById(alertaAtualizado.getAmbiente().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Ambiente não encontrado"));
            existing.setAmbiente(ambiente);

            // 2) Regras de negócio iguais à criação (por exemplo, riscoConfirmado + nível)
            if (Boolean.TRUE.equals(alertaAtualizado.getRiscoConfirmado())
                    && "BAIXO".equalsIgnoreCase(alertaAtualizado.getNivelRisco())) {
                throw new IllegalArgumentException("Alerta confirmado não pode ter nível de risco BAIXO.");
            }

            // Atualiza campos
            existing.setDataHora(alertaAtualizado.getDataHora());
            existing.setNivelRisco(alertaAtualizado.getNivelRisco());
            existing.setRiscoConfirmado(alertaAtualizado.getRiscoConfirmado());
            existing.setResponsavel(alertaAtualizado.getResponsavel());

            return alertaRepository.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("Alerta não encontrado"));
    }

    /**
     * Remove um Alerta existente. Se não encontrar, lança IllegalArgumentException.
     */
    @Transactional
    public void deletarAlerta(Long id) {
        Alerta toDelete = alertaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alerta não encontrado"));
        alertaRepository.delete(toDelete);
    }

    /**
     * Lista todos os Alertas de um determinado ambiente.
     */
    public List<Alerta> listarAlertasPorAmbiente(Long ambienteId) {
        Ambiente ambiente = ambienteRepository.findById(ambienteId)
                .orElseThrow(() -> new IllegalArgumentException("Ambiente não encontrado"));
        return alertaRepository.findByAmbiente(ambiente);
    }

    /**
     * Exemplo simples de busca de um Bombeiro disponível para o ambiente.
     * Poderia incluir lógica mais complexa (proximidade geográfica, turno etc.).
     */
    private Bombeiro buscarBombeiroDisponivelParaAmbiente(Ambiente ambiente) {
        return bombeiroRepository.findAll().stream().findFirst().orElse(null);
    }
}
