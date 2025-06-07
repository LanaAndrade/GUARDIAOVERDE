package com.guardiaoverde.guardiaoverde.controller;

import com.guardiaoverde.guardiaoverde.domain.PoliciaMilitar;
import com.guardiaoverde.guardiaoverde.service.PoliciaMilitarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/policias-militares")
public class PoliciaMilitarController {

    private final PoliciaMilitarService policiaService;

    @Autowired
    public PoliciaMilitarController(PoliciaMilitarService policiaService) {
        this.policiaService = policiaService;
    }

    /**
     * GET /v1/policias-militares
     * Lista todos os policiais militares.
     */
    @GetMapping
    public List<PoliciaMilitar> listarTodos() {
        return policiaService.listarTodos();
    }

    /**
     * GET /v1/policias-militares/{id}
     * Retorna um policial militar pelo seu ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            PoliciaMilitar pm = policiaService.buscarPorId(id);
            return ResponseEntity.ok(pm);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /v1/policias-militares
     * Cria um novo policial militar.
     */
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody PoliciaMilitar novaPolicia) {
        try {
            PoliciaMilitar salvo = policiaService.criarPolicia(novaPolicia);
            // Retorna 201 Created e a entidade criada.
            // Se quiser incluir Location, podemos fazer:
            URI location = URI.create("/v1/policias-militares/" + salvo.getId());
            return ResponseEntity.created(location).body(salvo);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * PUT /v1/policias-militares/{id}
     * Atualiza um policial militar existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody PoliciaMilitar dadosAtualizados) {
        try {
            PoliciaMilitar atualizado = policiaService.atualizarPolicia(id, dadosAtualizados);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage();
            if (msg.contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(msg);
        }
    }

    /**
     * DELETE /v1/policias-militares/{id}
     * Remove um policial militar pelo ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            policiaService.excluirPolicia(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /v1/policias-militares/buscar-por-matricula?matricula=...
     * Busca um policial militar pela matrícula.
     */
    @GetMapping("/buscar-por-matricula")
    public ResponseEntity<?> buscarPorMatricula(@RequestParam("matricula") String matricula) {
        return policiaService.buscarPorMatricula(matricula)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * GET /v1/policias-militares/buscar-por-nome?trecho=...
     * Lista policiais cujo nome contenha o trecho (ignorando maiúsculas/minúsculas).
     */
    @GetMapping("/buscar-por-nome")
    public List<PoliciaMilitar> buscarPorNome(@RequestParam("trecho") String trecho) {
        return policiaService.buscarPorTrechoNome(trecho);
    }
}
