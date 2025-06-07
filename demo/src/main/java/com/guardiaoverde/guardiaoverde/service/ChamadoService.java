package com.guardiaoverde.guardiaoverde.service;

import com.guardiaoverde.guardiaoverde.domain.Bombeiro;
import com.guardiaoverde.guardiaoverde.domain.Chamado;
import com.guardiaoverde.guardiaoverde.domain.Regiao;
import com.guardiaoverde.guardiaoverde.repository.BombeiroRepository;
import com.guardiaoverde.guardiaoverde.repository.ChamadoRepository;
import com.guardiaoverde.guardiaoverde.repository.RegiaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;
    private final RegiaoRepository regiaoRepository;
    private final BombeiroRepository bombeiroRepository;

    @Autowired
    public ChamadoService(ChamadoRepository chamadoRepository,
                          RegiaoRepository regiaoRepository,
                          BombeiroRepository bombeiroRepository) {
        this.chamadoRepository = chamadoRepository;
        this.regiaoRepository = regiaoRepository;
        this.bombeiroRepository = bombeiroRepository;
    }

    /**
     * Retorna todos os chamados.
     */
    public List<Chamado> listarTodos() {
        return chamadoRepository.findAll();
    }

    /**
     * Retorna um chamado pelo ID (lança IllegalArgumentException se não encontrar).
     */
    public Chamado buscarPorId(Long id) {
        return chamadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado"));
    }

    /**
     * Busca chamados por origem (ex.: "USUARIO" ou "SISTEMA").
     */
    public List<Chamado> buscarPorOrigem(String origem) {
        return chamadoRepository.findByOrigem(origem);
    }

    /**
     * Busca chamados por prioridade (ex.: "BAIXA", "MÉDIA", "ALTA").
     */
    public List<Chamado> buscarPorPrioridade(String prioridade) {
        return chamadoRepository.findByPrioridade(prioridade);
    }

    /**
     * Busca chamados cujo texto da descrição contenha o trecho informado.
     */
    public List<Chamado> buscarPorDescricao(String trecho) {
        return chamadoRepository.findByDescricaoContainingIgnoreCase(trecho);
    }

    /**
     * Lista todos os chamados de uma região específica.
     */
    public List<Chamado> listarPorRegiao(Long regiaoId) {
        Regiao r = regiaoRepository.findById(regiaoId)
                .orElseThrow(() -> new IllegalArgumentException("Região não encontrada"));
        return chamadoRepository.findByRegiao(r);
    }

    /**
     * Cria um novo Chamado, aplicando as regras de negócio definidas.
     * Lança IllegalArgumentException se alguma validação falhar.
     */
    @Transactional
    public Chamado criarChamado(Chamado novoChamado) {
        // 1) Validar existência da região
        Regiao r = regiaoRepository.findById(novoChamado.getRegiao().getId())
                .orElseThrow(() -> new IllegalArgumentException("Região não encontrada"));
        novoChamado.setRegiao(r);

        // 2) dataHora não pode ser futura
        if (novoChamado.getDataHora().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data e hora do chamado não pode ser no futuro.");
        }

        // 3) Se origem = “SISTEMA”, descrição deve conter “SENSOR”
        if ("SISTEMA".equalsIgnoreCase(novoChamado.getOrigem())
                && !novoChamado.getDescricao().toUpperCase().contains("SENSOR")) {
            throw new IllegalArgumentException(
                    "Chamado de SISTEMA deve conter referência a SENSOR na descrição.");
        }

        // 4) Ajuste automático de prioridade baseado em índice de secura
        if (r.getIndiceSecura() > 0.8
                && "BAIXA".equalsIgnoreCase(novoChamado.getPrioridade())) {
            novoChamado.setPrioridade("MEDIA");
        }

        // 5) Evitar duplicidade de chamados na mesma região em um intervalo de 10 min
        List<Chamado> ultimos = chamadoRepository.findTopByRegiaoOrderByDataHoraDesc(r);
        if (!ultimos.isEmpty()) {
            Chamado ultimo = ultimos.get(0);
            Duration diff = Duration.between(ultimo.getDataHora(), novoChamado.getDataHora());
            if (diff.toMinutes() < 10
                    && ultimo.getDescricao().equalsIgnoreCase(novoChamado.getDescricao())) {
                throw new IllegalArgumentException(
                        "Já existe um chamado com mesma descrição nesta região nos últimos 10 minutos.");
            }
        }

        // 6) Se prioridade = ALTA, associar um bombeiro disponível
        if ("ALTA".equalsIgnoreCase(novoChamado.getPrioridade())) {
            Bombeiro responsavel = buscarBombeiroParaRegiao(r);
            if (responsavel != null) {
                // Aqui poderíamos armazenar o usuário responsável ou notificar externamente
                // Exemplo: novoChamado.setResponsavel(responsavel.getUsuario());
            }
        }

        return chamadoRepository.save(novoChamado);
    }

    /**
     * Atualiza um Chamado existente. Lança IllegalArgumentException se não encontrar ou falhar validação.
     */
    @Transactional
    public Chamado atualizarChamado(Long id, Chamado dadosChamado) {
        Chamado existente = buscarPorId(id); // já lança se não existir

        // Reaplica basicamente as mesmas validações de data/hora e origem
        if (dadosChamado.getDataHora().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data e hora do chamado não pode ser no futuro.");
        }
        if ("SISTEMA".equalsIgnoreCase(dadosChamado.getOrigem())
                && !dadosChamado.getDescricao().toUpperCase().contains("SENSOR")) {
            throw new IllegalArgumentException(
                    "Chamado de SISTEMA deve conter referência a SENSOR na descrição.");
        }

        // Caso a região tenha mudado
        if (!existente.getRegiao().getId().equals(dadosChamado.getRegiao().getId())) {
            Regiao novaRegiao = regiaoRepository.findById(dadosChamado.getRegiao().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Região não encontrada"));
            existente.setRegiao(novaRegiao);
        }

        existente.setOrigem(dadosChamado.getOrigem());
        existente.setDescricao(dadosChamado.getDescricao());
        existente.setDataHora(dadosChamado.getDataHora());
        existente.setPrioridade(dadosChamado.getPrioridade());

        // Poderíamos repetir a regra de ajuste de prioridade aqui, se necessário
        if (existente.getRegiao().getIndiceSecura() > 0.8
                && "BAIXA".equalsIgnoreCase(existente.getPrioridade())) {
            existente.setPrioridade("MEDIA");
        }

        return chamadoRepository.save(existente);
    }

    /**
     * Remove um Chamado pelo ID. Lança IllegalArgumentException se não encontrar.
     */
    @Transactional
    public void excluirChamado(Long id) {
        Chamado existente = buscarPorId(id);
        chamadoRepository.delete(existente);
    }

    /**
     * Método fictício que retorna o primeiro bombeiro disponível para uma região.
     * Poderia ser expandido usando rotas, proximidade geográfica etc.
     */
    private Bombeiro buscarBombeiroParaRegiao(Regiao r) {
        return bombeiroRepository.findAll().stream().findFirst().orElse(null);
    }
}
