package com.guardiaoverde.guardiaoverde.controller;

import com.guardiaoverde.guardiaoverde.domain.Rota;
import com.guardiaoverde.guardiaoverde.service.RotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/rotas")
public class RotaController {

    private final RotaService rotaService;

    @Autowired
    public RotaController(RotaService rotaService) {
        this.rotaService = rotaService;
    }

    /**
     * GET /v1/rotas
     * Retorna todas as rotas.
     */
    @GetMapping
    public List<Rota> listarTodas() {
        return rotaService.listarRotas();
    }

    /**
     * GET /v1/rotas/{id}
     * Retorna uma rota pelo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Rota r = rotaService.findById(id);
            return ResponseEntity.ok(r);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /v1/rotas
     * Cria uma nova rota.
     */
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Rota novaRota) {
        try {
            Rota salva = rotaService.salvarRota(novaRota);
            URI location = URI.create("/v1/rotas/" + salva.getId());
            return ResponseEntity.created(location).body(salva);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * PUT /v1/rotas/{id}
     * Atualiza uma rota existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody Rota rotaAtualizada
    ) {
        try {
            // certifica-se de que ela existe ou lança IllegalArgumentException
            Rota existente = rotaService.findById(id);

            existente.setPontoPartida(rotaAtualizada.getPontoPartida());
            existente.setPontoDestino(rotaAtualizada.getPontoDestino());
            existente.setTempoEstimado(rotaAtualizada.getTempoEstimado());
            existente.setDistancia(rotaAtualizada.getDistancia());
            existente.setCaminhosAlternativos(rotaAtualizada.getCaminhosAlternativos());

            Rota salva = rotaService.salvarRota(existente);
            return ResponseEntity.ok(salva);
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage();
            if (msg.equals("Rota não encontrada")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(msg);
        }
    }

    /**
     * DELETE /v1/rotas/{id}
     * Remove uma rota pelo ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            rotaService.excluirRota(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
