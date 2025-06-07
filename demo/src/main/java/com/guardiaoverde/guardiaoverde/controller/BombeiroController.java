package com.guardiaoverde.guardiaoverde.controller;

import com.guardiaoverde.guardiaoverde.domain.Bombeiro;
import com.guardiaoverde.guardiaoverde.service.BombeiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/bombeiros")
public class BombeiroController {

    private final BombeiroService bombeiroService;

    @Autowired
    public BombeiroController(BombeiroService bombeiroService) {
        this.bombeiroService = bombeiroService;
    }

    /**
     * GET /v1/bombeiros
     * Retorna todos os bombeiros.
     */
    @GetMapping
    public List<Bombeiro> listarTodos() {
        return bombeiroService.listarTodos();
    }

    /**
     * GET /v1/bombeiros/{id}
     * Retorna um bombeiro por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Bombeiro b = bombeiroService.buscarPorId(id);
            return ResponseEntity.ok(b);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /v1/bombeiros/buscarPorNome?trecho={trecho}
     * Busca bombeiros cujo nome contenha o trecho fornecido.
     */
    @GetMapping("/buscarPorNome")
    public List<Bombeiro> buscarPorNome(@RequestParam("trecho") String trecho) {
        return bombeiroService.buscarPorNome(trecho);
    }

    /**
     * GET /v1/bombeiros/turno/{turno}
     * Busca bombeiros que atuam no turno especificado.
     */
    @GetMapping("/turno/{turno}")
    public List<Bombeiro> buscarPorTurno(@PathVariable String turno) {
        return bombeiroService.buscarPorTurno(turno);
    }

    /**
     * POST /v1/bombeiros
     * Cria um novo bombeiro.
     */
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Bombeiro bombeiro) {
        try {
            Bombeiro salvo = bombeiroService.criarBombeiro(bombeiro);
            return ResponseEntity.ok(salvo);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * PUT /v1/bombeiros/{id}
     * Atualiza um bombeiro existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id,
                                       @RequestBody Bombeiro dados) {
        try {
            Bombeiro atualizado = bombeiroService.atualizarBombeiro(id, dados);
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
     * DELETE /v1/bombeiros/{id}
     * Exclui um bombeiro pelo ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            bombeiroService.excluirBombeiro(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
