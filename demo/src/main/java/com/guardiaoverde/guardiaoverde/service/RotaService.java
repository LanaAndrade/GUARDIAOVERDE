package com.guardiaoverde.guardiaoverde.service;

import com.guardiaoverde.guardiaoverde.domain.Rota;
import com.guardiaoverde.guardiaoverde.repository.RotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RotaService {

    private final RotaRepository rotaRepository;

    @Autowired
    public RotaService(RotaRepository rotaRepository) {
        this.rotaRepository = rotaRepository;
    }

    /**
     * Cria ou atualiza uma Rota, validando valores básicos.
     */
    @Transactional
    public Rota salvarRota(Rota r) {
        if (r.getTempoEstimado() < 0) {
            throw new IllegalArgumentException("Tempo estimado não pode ser negativo.");
        }
        if (r.getDistancia() < 0) {
            throw new IllegalArgumentException("Distância não pode ser negativa.");
        }
        return rotaRepository.save(r);
    }

    /**
     * Retorna todas as rotas.
     */
    public List<Rota> listarRotas() {
        return rotaRepository.findAll();
    }

    /**
     * Busca uma rota por ID. Lança IllegalArgumentException se não existir.
     */
    public Rota findById(Long id) {
        return rotaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rota não encontrada"));
    }

    /**
     * Remove uma rota; lança IllegalArgumentException se não existir.
     */
    @Transactional
    public void excluirRota(Long id) {
        Rota existente = findById(id);
        rotaRepository.delete(existente);
    }
}
