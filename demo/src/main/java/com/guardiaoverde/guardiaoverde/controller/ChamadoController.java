package com.guardiaoverde.guardiaoverde.controller;

import com.guardiaoverde.guardiaoverde.domain.Chamado;
import com.guardiaoverde.guardiaoverde.service.ChamadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/chamados")
public class ChamadoController {

    private final ChamadoService chamadoService;

    @Autowired
    public ChamadoController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    /**
     * GET /v1/chamados
     * Lista todos os chamados.
     */
    @GetMapping
    public List<Chamado> listarTodos() {
        return chamadoService.listarTodos();
    }

    /**
     * GET /v1/chamados/{id}
     * Busca um chamado pelo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Chamado c = chamadoService.buscarPorId(id);
            return ResponseEntity.ok(c);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /v1/chamados
     * Cria um novo chamado.
     *
     * Exemplo de JSON no corpo:
     * {
     *   "origem": "USUARIO",
     *   "descricao": "Fumaça detectada",
     *   "regiao": { "id": 3 },
     *   "dataHora": "2025-06-04T21:00:00",
     *   "prioridade": "ALTA"
     * }
     */
    @PostMapping
    public ResponseEntity<?> criarChamado(@RequestBody Chamado chamado) {
        try {
            Chamado criado = chamadoService.criarChamado(chamado);
            return ResponseEntity.status(201).body(criado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * PUT /v1/chamados/{id}
     * Atualiza um chamado existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarChamado(
            @PathVariable Long id,
            @RequestBody Chamado dadosChamado) {
        try {
            Chamado atualizado = chamadoService.atualizarChamado(id, dadosChamado);
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
     * DELETE /v1/chamados/{id}
     * Remove um chamado.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChamado(@PathVariable Long id) {
        try {
            chamadoService.excluirChamado(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /v1/chamados/origem/{origem}
     * Retorna todos os chamados cuja origem é exatamente o parâmetro.
     */
    @GetMapping("/origem/{origem}")
    public List<Chamado> buscarPorOrigem(@PathVariable String origem) {
        return chamadoService.buscarPorOrigem(origem);
    }

    /**
     * GET /v1/chamados/prioridade/{prioridade}
     * Retorna todos os chamados com prioridade exata.
     */
    @GetMapping("/prioridade/{prioridade}")
    public List<Chamado> buscarPorPrioridade(@PathVariable String prioridade) {
        return chamadoService.buscarPorPrioridade(prioridade);
    }

    /**
     * GET /v1/chamados/busca/descricao?trecho=...
     * Retorna todos os chamados cuja descrição contém o trecho (case-insensitive).
     */
    @GetMapping("/busca/descricao")
    public List<Chamado> buscarPorDescricao(@RequestParam("trecho") String trecho) {
        return chamadoService.buscarPorDescricao(trecho);
    }

    /**
     * GET /v1/chamados/regiao/{regiaoId}
     * Retorna todos os chamados da região especificada (pelo ID da região).
     */
    @GetMapping("/regiao/{regiaoId}")
    public ResponseEntity<?> buscarPorRegiao(@PathVariable Long regiaoId) {
        try {
            List<Chamado> lista = chamadoService.listarPorRegiao(regiaoId);
            return ResponseEntity.ok(lista);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
