package com.guardiaoverde.guardiaoverde.controller;

import com.guardiaoverde.guardiaoverde.domain.Alerta;
import com.guardiaoverde.guardiaoverde.service.AlertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller REST para gerenciar Alertas via AlertaService.
 */
@RestController
@RequestMapping("/v1/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    @Autowired
    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    /**
     * GET /v1/alertas
     * Lista todos os alertas.
     */
    @GetMapping
    public List<Alerta> listarTodos() {
        return alertaService.listarTodos();
    }

    /**
     * GET /v1/alertas/{id}
     * Retorna um alerta pelo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Alerta> buscarPorId(@PathVariable Long id) {
        Optional<Alerta> alertaOpt = alertaService.buscarPorId(id);
        return alertaOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * POST /v1/alertas
     * Cria um novo alerta.
     *
     * Exemplo de JSON de entrada:
     * {
     *   "dataHora": "2025-06-02T21:30:00",
     *   "nivelRisco": "ALTO",
     *   "riscoConfirmado": true,
     *   "ambiente": { "id": 3 },
     *   "responsavel": { "id": 5 }
     * }
     */
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Alerta novoAlerta) {
        try {
            Alerta salvo = alertaService.criarAlerta(novoAlerta);
            return ResponseEntity.ok(salvo);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * PUT /v1/alertas/{id}
     * Atualiza completamente um alerta existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody Alerta alertaAtualizado) {

        try {
            Alerta salvo = alertaService.atualizarAlerta(id, alertaAtualizado);
            return ResponseEntity.ok(salvo);
        } catch (IllegalArgumentException ex) {
            // Se o ID não existir, ou alguma regra for violada, retorna 400 com mensagem
            if (ex.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * DELETE /v1/alertas/{id}
     * Remove um alerta existente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            alertaService.deletarAlerta(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /v1/alertas/ambiente/{ambienteId}
     * Lista todos os alertas de um determinado ambiente.
     */
    @GetMapping("/ambiente/{ambienteId}")
    public ResponseEntity<?> listarPorAmbiente(@PathVariable Long ambienteId) {
        try {
            List<Alerta> lista = alertaService.listarAlertasPorAmbiente(ambienteId);
            return ResponseEntity.ok(lista);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
