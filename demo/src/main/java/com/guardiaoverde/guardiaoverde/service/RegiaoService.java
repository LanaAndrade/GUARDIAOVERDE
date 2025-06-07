package com.guardiaoverde.guardiaoverde.service;

import com.guardiaoverde.guardiaoverde.domain.Regiao;
import com.guardiaoverde.guardiaoverde.repository.RegiaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegiaoService {

    private final RegiaoRepository regiaoRepository;

    @Autowired
    public RegiaoService(RegiaoRepository regiaoRepository) {
        this.regiaoRepository = regiaoRepository;
    }

    /**
     * Lista todas as regiões.
     */
    public List<Regiao> listarTodas() {
        return regiaoRepository.findAll();
    }

    /**
     * Busca uma região pelo ID. Lança IllegalArgumentException se não existir.
     */
    public Regiao findById(Long id) {
        return regiaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Região não encontrada"));
    }

    /**
     * Busca regiões cujo nome contenha o trecho informado (ignorando maiúsculas/minúsculas).
     */
    public List<Regiao> buscarPorTrechoNome(String trecho) {
        return regiaoRepository.findByNomeContainingIgnoreCase(trecho);
    }

    /**
     * Cria ou atualiza uma Região, validando campos básicos.
     */
    @Transactional
    public Regiao salvarRegiao(Regiao r) {
        // 3.1: Validar índice de secura
        if (r.getIndiceSecura() < 0.0 || r.getIndiceSecura() > 1.0) {
            throw new IllegalArgumentException("Índice de secura deve estar entre 0.0 e 1.0.");
        }
        // 3.1: Tipo de vegetação válido
        String tipo = r.getTipoVegetacao().toUpperCase();
        if (!(tipo.equals("FLORESTA") || tipo.equals("CERRADO") || tipo.equals("CAATINGA") || tipo.equals("MATA ATLÂNTICA"))) {
            throw new IllegalArgumentException("Tipo de vegetação inválido: " + tipo);
        }
        return regiaoRepository.save(r);
    }

    /**
     * Exclui uma região. Lança IllegalArgumentException se não existir.
     */
    @Transactional
    public void excluirRegiao(Long id) {
        Regiao existente = findById(id);
        regiaoRepository.delete(existente);
    }
}
