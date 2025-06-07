package com.guardiaoverde.guardiaoverde.controller;

import com.guardiaoverde.guardiaoverde.domain.Ambiente;
import com.guardiaoverde.guardiaoverde.service.AmbienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/ambientes")
public class AmbienteController {

    private final AmbienteService ambienteService;

    @Autowired
    public AmbienteController(AmbienteService ambienteService) {
        this.ambienteService = ambienteService;
    }

    /**
     * GET /v1/ambientes
     * Lista todos os ambientes.
     */
    @GetMapping
    public List<Ambiente> listarTodos() {
        return ambienteService.listarTodos();
    }

    /**
     * GET /v1/ambientes/{id}
     * Retorna um ambiente pelo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Ambiente encontrado = ambienteService.findById(id);
            return ResponseEntity.ok(encontrado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /v1/ambientes
     * Cria um novo ambiente. Validações ficam no service.
     */
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Ambiente ambiente) {
        try {
            Ambiente salvo = ambienteService.salvarAmbiente(ambiente);
            return ResponseEntity.ok(salvo);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * PUT /v1/ambientes/{id}
     * Atualiza um ambiente existente (substituição total dos campos).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody Ambiente dados) {

        try {
            // Primeiro, busca o registro existente (lança IllegalArgumentException se não achar)
            Ambiente existente = ambienteService.findById(id);

            // Copia os novos valores e chama o service para validar e salvar
            existente.setClima(dados.getClima());
            existente.setTemperatura(dados.getTemperatura());
            existente.setUmidade(dados.getUmidade());
            existente.setLocalizacao(dados.getLocalizacao());

            Ambiente atualizado = ambienteService.salvarAmbiente(existente);
            return ResponseEntity.ok(atualizado);

        } catch (IllegalArgumentException ex) {
            // Se não encontrar, retornamos 404; se violar regra, retornamos 400 com mensagem
            String msg = ex.getMessage();
            if (msg.contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(msg);
        }
    }

    /**
     * DELETE /v1/ambientes/{id}
     * Exclui um ambiente pelo ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            // Tenta buscar para validar existência
            ambienteService.findById(id);
            ambienteService.salvarAmbiente(null); // não usamos para deletar, mas apenas validar antes
            // Para excluir, podemos chamar diretamente o repositório ou criar método no service.
            // Se quiser usar só service, adicione um método deletarAmbiente(id) lá. Aqui vamos chamar deleteById via serviço:
            ambienteService.excluirPorId(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
