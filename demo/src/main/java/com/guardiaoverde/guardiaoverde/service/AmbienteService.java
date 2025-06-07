package com.guardiaoverde.guardiaoverde.service;

import com.guardiaoverde.guardiaoverde.domain.Ambiente;
import com.guardiaoverde.guardiaoverde.repository.AmbienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AmbienteService {

    private final AmbienteRepository ambienteRepository;

    @Autowired
    public AmbienteService(AmbienteRepository ambienteRepository) {
        this.ambienteRepository = ambienteRepository;
    }

    /**
     * Cria ou atualiza um Ambiente, validando campos mínimos.
     */
    @Transactional
    public Ambiente salvarAmbiente(Ambiente a) {
        if (a == null) {
            throw new IllegalArgumentException("Ambiente não encontrado");
        }
        // 2.1: Verificar valores plausíveis de temperatura e umidade
        if (a.getTemperatura() < -50.0 || a.getTemperatura() > 60.0) {
            throw new IllegalArgumentException("Temperatura fora da faixa permitida.");
        }
        if (a.getUmidade() < 0.0 || a.getUmidade() > 100.0) {
            throw new IllegalArgumentException("Umidade fora da faixa permitida.");
        }

        // 2.1: Validar clima em um conjunto pré-definido (exemplo simples abaixo)
        String clima = a.getClima().toUpperCase();
        if (!(clima.equals("ENSOLARADO") || clima.equals("CHUVOSO")
                || clima.equals("NUBLADO") || clima.equals("VARIAVEL"))) {
            throw new IllegalArgumentException("Clima inválido: " + clima);
        }

        return ambienteRepository.save(a);
    }

    public List<Ambiente> listarTodos() {
        return ambienteRepository.findAll();
    }

    public Ambiente findById(Long id) {
        return ambienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ambiente não encontrado"));
    }

    @Transactional
    public void excluirPorId(Long id) {
        Ambiente existente = ambienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ambiente não encontrado"));
        ambienteRepository.delete(existente);
    }
}
