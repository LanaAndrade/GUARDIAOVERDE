package com.guardiaoverde.guardiaoverde.controller;

import com.guardiaoverde.guardiaoverde.domain.Usuario;
import com.guardiaoverde.guardiaoverde.service.UsuarioService;
import com.guardiaoverde.guardiaoverde.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioRepository usuarioRepository,
                             UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * GET /v1/usuarios
     * Lista todos os usuários (sem regras extras).
     */
    @GetMapping
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * GET /v1/usuarios/{id}
     * Busca um usuário por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        return opt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * POST /v1/usuarios?executorId={executorId}
     * Cria um novo usuário. Somente executores com papel ADMIN podem criar.
     *
     * Exemplo de chamada:
     * POST /v1/usuarios?executorId=1
     * Body JSON:
     * {
     *   "nome": "Maria Oliveira",
     *   "email": "maria.oliveira@exemplo.com",
     *   "senha": "senha123",
     *   "papel": "ADMIN"
     * }
     */
    @PostMapping
    public ResponseEntity<?> criar(
            @RequestParam("executorId") Long executorId,
            @RequestBody Usuario usuario) {

        // Busca executor no banco
        Optional<Usuario> optExecutor = usuarioRepository.findById(executorId);
        if (optExecutor.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Executor não encontrado");
        }

        try {
            Usuario criado = usuarioService.criarUsuario(usuario, optExecutor.get());
            URI location = URI.create("/v1/usuarios/" + criado.getId());
            return ResponseEntity.created(location).body(criado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * PUT /v1/usuarios/{id}?executorId={executorId}
     * Atualiza um usuário existente. Somente ADMIN ou o próprio usuário podem atualizar.
     *
     * Exemplo de chamada:
     * PUT /v1/usuarios/5?executorId=1
     * Body JSON:
     * {
     *   "nome": "João Silva",
     *   "email": "joao.silva@exemplo.com",
     *   "senha": "novaSenha",
     *   "papel": "OPERADOR"
     * }
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestParam("executorId") Long executorId,
            @RequestBody Usuario dados) {

        // Busca executor no banco
        Optional<Usuario> optExecutor = usuarioRepository.findById(executorId);
        if (optExecutor.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Executor não encontrado");
        }

        try {
            Usuario atualizado = usuarioService.atualizarUsuario(id, dados, optExecutor.get());
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage();
            if (msg.equals("Usuário não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(msg);
        }
    }

    /**
     * DELETE /v1/usuarios/{id}
     * Exclui um usuário. Se não existir, retorna 404.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
