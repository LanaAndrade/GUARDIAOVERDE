package com.guardiaoverde.guardiaoverde.service;

import com.guardiaoverde.guardiaoverde.domain.Bombeiro;
import com.guardiaoverde.guardiaoverde.domain.Usuario;
import com.guardiaoverde.guardiaoverde.repository.BombeiroRepository;
import com.guardiaoverde.guardiaoverde.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BombeiroService {

    private final BombeiroRepository bombeiroRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public BombeiroService(BombeiroRepository bombeiroRepository,
                           UsuarioRepository usuarioRepository) {
        this.bombeiroRepository = bombeiroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Retorna todos os bombeiros cadastrados.
     */
    public List<Bombeiro> listarTodos() {
        return bombeiroRepository.findAll();
    }

    /**
     * Retorna um bombeiro pelo ID (lança exceção se não encontrar).
     */
    public Bombeiro buscarPorId(Long id) {
        return bombeiroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bombeiro não encontrado"));
    }

    /**
     * Busca bombeiros cujo nome contenha o trecho (ignora maiúsculas/minúsculas).
     */
    public List<Bombeiro> buscarPorNome(String trecho) {
        return bombeiroRepository.findByNomeContainingIgnoreCase(trecho);
    }

    /**
     * Busca bombeiros por turno (ex.: "Manhã", "Noite").
     */
    public List<Bombeiro> buscarPorTurno(String turno) {
        return bombeiroRepository.findByTurno(turno);
    }

    /**
     * Cria um novo Bombeiro.
     * Se estiver vinculado a um usuário, valida existência e papel.
     */
    @Transactional
    public Bombeiro criarBombeiro(Bombeiro b) {
        if (b.getNome() == null || b.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do bombeiro é obrigatório.");
        }
        if (b.getTurno() == null || b.getTurno().isBlank()) {
            throw new IllegalArgumentException("Turno do bombeiro é obrigatório.");
        }

        if (b.getUsuario() != null) {
            Usuario u = usuarioRepository.findById(b.getUsuario().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            // Garante que o papel do usuário seja BOMBEIRO
            if (!"BOMBEIRO".equalsIgnoreCase(u.getPapel())) {
                throw new IllegalArgumentException("O usuário precisa ter papel BOMBEIRO.");
            }
            b.setUsuario(u);
        }

        return bombeiroRepository.save(b);
    }

    /**
     * Atualiza um bombeiro existente (lança exceção se não encontrar).
     */
    @Transactional
    public Bombeiro atualizarBombeiro(Long id, Bombeiro dados) {
        Bombeiro existente = buscarPorId(id);

        if (dados.getNome() != null && !dados.getNome().isBlank()) {
            existente.setNome(dados.getNome());
        } else {
            throw new IllegalArgumentException("Nome do bombeiro é obrigatório.");
        }

        if (dados.getTurno() != null && !dados.getTurno().isBlank()) {
            existente.setTurno(dados.getTurno());
        } else {
            throw new IllegalArgumentException("Turno do bombeiro é obrigatório.");
        }

        existente.setTelefone(dados.getTelefone());

        if (dados.getUsuario() != null) {
            Usuario u = usuarioRepository.findById(dados.getUsuario().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            if (!"BOMBEIRO".equalsIgnoreCase(u.getPapel())) {
                throw new IllegalArgumentException("O usuário precisa ter papel BOMBEIRO.");
            }
            existente.setUsuario(u);
        } else {
            existente.setUsuario(null);
        }

        return bombeiroRepository.save(existente);
    }

    /**
     * Remove um bombeiro pelo ID (lança exceção se não encontrar).
     */
    @Transactional
    public void excluirBombeiro(Long id) {
        Bombeiro existente = buscarPorId(id);
        bombeiroRepository.delete(existente);
    }
}
