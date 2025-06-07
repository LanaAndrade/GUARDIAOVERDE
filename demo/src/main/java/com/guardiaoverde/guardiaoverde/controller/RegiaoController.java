package com.guardiaoverde.guardiaoverde.controller;

import com.guardiaoverde.guardiaoverde.domain.Regiao;
import com.guardiaoverde.guardiaoverde.service.RegiaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/regioes")
public class RegiaoController {

    private final RegiaoService regiaoService;

    @Autowired
    public RegiaoController(RegiaoService regiaoService) {
        this.regiaoService = regiaoService;
    }

    /**
     * GET /v1/regioes
     * Retorna todas as regiões.
     */
    @GetMapping
    public List<Regiao> listarTodas() {
        return regiaoService.listarTodas();
    }

    /**
     * GET /v1/regioes/{id}
     * Busca uma região por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Regiao reg = regiaoService.findById(id);
            return ResponseEntity.ok(reg);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /v1/regioes/buscar?nome=trecho
     * Busca regiões cujo nome contenha o trecho informado.
     */
    @GetMapping("/buscar")
    public List<Regiao> buscarPorNome(@RequestParam("nome") String trecho) {
        return regiaoService.buscarPorTrechoNome(trecho);
    }

    /**
     * POST /v1/regioes
     * Cria uma nova região (ou atualiza, se já existir ID no payload).
     */
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Regiao regiao) {
        try {
            Regiao salvo = regiaoService.salvarRegiao(regiao);
            URI location = URI.create("/v1/regioes/" + salvo.getId());
            return ResponseEntity.created(location).body(salvo);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * PUT /v1/regioes/{id}
     * Atualiza uma região existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody Regiao regiaoAtualizada) {
        try {
            // Verifica se existe; se não existir, findById lança IllegalArgumentException
            Regiao existente = regiaoService.findById(id);
            // Copia valores do payload para a entidade existente
            existente.setNome(regiaoAtualizada.getNome());
            existente.setLimitesGeograficos(regiaoAtualizada.getLimitesGeograficos());
            existente.setTipoVegetacao(regiaoAtualizada.getTipoVegetacao());
            existente.setIndiceSecura(regiaoAtualizada.getIndiceSecura());
            // Salva (vai executar as mesmas validações de salvarRegiao)
            Regiao salvo = regiaoService.salvarRegiao(existente);
            return ResponseEntity.ok(salvo);
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage();
            if (msg.equals("Região não encontrada")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(msg);
        }
    }

    /**
     * DELETE /v1/regioes/{id}
     * Remove uma região por ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            regiaoService.excluirRegiao(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
