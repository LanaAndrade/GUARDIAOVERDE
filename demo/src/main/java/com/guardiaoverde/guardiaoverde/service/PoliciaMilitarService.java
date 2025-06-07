package com.guardiaoverde.guardiaoverde.service;

import com.guardiaoverde.guardiaoverde.domain.PoliciaMilitar;
import com.guardiaoverde.guardiaoverde.domain.Usuario;
import com.guardiaoverde.guardiaoverde.repository.PoliciaMilitarRepository;
import com.guardiaoverde.guardiaoverde.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PoliciaMilitarService {

    private final PoliciaMilitarRepository policiaMilitarRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public PoliciaMilitarService(PoliciaMilitarRepository policiaMilitarRepository,
                                 UsuarioRepository usuarioRepository) {
        this.policiaMilitarRepository = policiaMilitarRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Lista todos os policiais militares.
     */
    public List<PoliciaMilitar> listarTodos() {
        return policiaMilitarRepository.findAll();
    }

    /**
     * Busca um policial militar pelo ID.
     * Lança IllegalArgumentException se não encontrar.
     */
    public PoliciaMilitar buscarPorId(Long id) {
        return policiaMilitarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Policial Militar não encontrado"));
    }

    /**
     * Cria um novo policial militar. Se vier vinculado a um usuário, garante papel "POLICIAL"
     * e matrícula única.
     */
    @Transactional
    public PoliciaMilitar criarPolicia(PoliciaMilitar pm) {
        // Validar nome e matrícula não nulos/embeçados
        if (pm.getNome() == null || pm.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do policial é obrigatório.");
        }
        if (pm.getMatricula() == null || pm.getMatricula().isBlank()) {
            throw new IllegalArgumentException("Matrícula é obrigatória.");
        }
        // Verificar unicidade de matrícula
        if (policiaMilitarRepository.existsByMatricula(pm.getMatricula())) {
            throw new IllegalArgumentException("Matrícula já cadastrada.");
        }

        // Se veio vinculado a um usuário, garantir que exista e tenha papel "POLICIAL"
        if (pm.getUsuario() != null) {
            Usuario u = usuarioRepository.findById(pm.getUsuario().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            if (!"POLICIAL".equalsIgnoreCase(u.getPapel())) {
                throw new IllegalArgumentException("O usuário precisa ter papel POLICIAL.");
            }
            pm.setUsuario(u);
        }

        return policiaMilitarRepository.save(pm);
    }

    /**
     * Atualiza completamente um policial militar existente.
     * Lança IllegalArgumentException se não encontrar ou falhar validação.
     */
    @Transactional
    public PoliciaMilitar atualizarPolicia(Long id, PoliciaMilitar dadosAtualizados) {
        PoliciaMilitar existente = buscarPorId(id);

        // Se houve troca de matrícula, verificar se a nova é única
        if (!existente.getMatricula().equals(dadosAtualizados.getMatricula())) {
            String novaMat = dadosAtualizados.getMatricula();
            if (novaMat == null || novaMat.isBlank()) {
                throw new IllegalArgumentException("Matrícula obrigatória.");
            }
            if (policiaMilitarRepository.existsByMatricula(novaMat)) {
                throw new IllegalArgumentException("Matrícula já cadastrada.");
            }
            existente.setMatricula(novaMat);
        }

        // Atualiza nome e telefone
        existente.setNome(dadosAtualizados.getNome());
        existente.setTelefone(dadosAtualizados.getTelefone());

        // Se trocar vínculo de usuário, garante existência e papel correto
        if (dadosAtualizados.getUsuario() != null) {
            Usuario u = usuarioRepository.findById(dadosAtualizados.getUsuario().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            if (!"POLICIAL".equalsIgnoreCase(u.getPapel())) {
                throw new IllegalArgumentException("O usuário precisa ter papel POLICIAL.");
            }
            existente.setUsuario(u);
        } else {
            existente.setUsuario(null);
        }

        return policiaMilitarRepository.save(existente);
    }

    /**
     * Remove um policial militar pelo ID.
     * Lança IllegalArgumentException se não encontrar.
     */
    @Transactional
    public void excluirPolicia(Long id) {
        PoliciaMilitar existente = buscarPorId(id);
        policiaMilitarRepository.delete(existente);
    }

    /**
     * Busca um policial militar por matrícula.
     */
    public Optional<PoliciaMilitar> buscarPorMatricula(String matricula) {
        return policiaMilitarRepository.findByMatricula(matricula);
    }

    /**
     * Busca policiais cujo nome contenha o trecho informado (case-insensitive).
     */
    public List<PoliciaMilitar> buscarPorTrechoNome(String trecho) {
        return policiaMilitarRepository.findByNomeContainingIgnoreCase(trecho);
    }

    /**
     * Verifica se um dado usuário já está vinculado a algum policial militar.
     */
    public Optional<PoliciaMilitar> findByUsuario(Usuario u) {
        return policiaMilitarRepository.findByUsuario(u);
    }
}
