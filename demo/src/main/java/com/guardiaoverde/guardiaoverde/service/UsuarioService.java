package com.guardiaoverde.guardiaoverde.service;

import com.guardiaoverde.guardiaoverde.domain.Usuario;
import com.guardiaoverde.guardiaoverde.domain.PoliciaMilitar;
import com.guardiaoverde.guardiaoverde.domain.Bombeiro;
import com.guardiaoverde.guardiaoverde.repository.UsuarioRepository;
import com.guardiaoverde.guardiaoverde.repository.PoliciaMilitarRepository;
import com.guardiaoverde.guardiaoverde.repository.BombeiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PoliciaMilitarRepository policiaMilitarRepository;
    private final BombeiroRepository bombeiroRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
                          PoliciaMilitarRepository policiaMilitarRepository,
                          BombeiroRepository bombeiroRepository) {
        this.usuarioRepository = usuarioRepository;
        this.policiaMilitarRepository = policiaMilitarRepository;
        this.bombeiroRepository = bombeiroRepository;
    }

    /**
     * Cria um novo usuário. Somente usuários com papel ADMIN podem chamar este método.
     */
    @Transactional
    public Usuario criarUsuario(Usuario novoUsuario, Usuario executor) {
        // 1.1: Apenas ADMIN pode criar usuários
        if (!"ADMIN".equalsIgnoreCase(executor.getPapel())) {
            throw new IllegalArgumentException("Somente ADMIN pode criar usuários.");
        }

        // 1.1: Validar unicidade de e-mail
        if (usuarioRepository.existsByEmail(novoUsuario.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        // 1.1: Validar papel (exemplos permitidos: ADMIN, OPERADOR, CONVIDADO)
        String papel = novoUsuario.getPapel().toUpperCase();
        if (!(papel.equals("ADMIN") || papel.equals("OPERADOR") || papel.equals("CONVIDADO"))) {
            throw new IllegalArgumentException("Papel inválido: " + papel);
        }

        // Aqui, em produção, aplicar hash na senha antes de salvar
        // novoUsuario.setSenha(hashService.hash(novoUsuario.getSenha()));

        return usuarioRepository.save(novoUsuario);
    }

    /**
     * Atualiza informações básicas de usuário. Somente ADMIN ou o próprio usuário podem atualizar.
     */
    @Transactional
    public Usuario atualizarUsuario(Long id, Usuario dadosAtualizados, Usuario executor) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // 1.1: Somente ADMIN ou o próprio usuário
        if (!"ADMIN".equalsIgnoreCase(executor.getPapel()) && !executor.getId().equals(id)) {
            throw new IllegalArgumentException("Você não tem permissão para atualizar este usuário.");
        }

        // Se estiver atualizando email, verificar unicidade
        if (!existente.getEmail().equals(dadosAtualizados.getEmail()) &&
                usuarioRepository.existsByEmail(dadosAtualizados.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        existente.setNome(dadosAtualizados.getNome());
        existente.setEmail(dadosAtualizados.getEmail());
        existente.setPapel(dadosAtualizados.getPapel().toUpperCase());
        // NÃO atualizamos a senha aqui sem lógica específica de troca de senha

        return usuarioRepository.save(existente);
    }

    /**
     * Vincula um usuário existente a um registro de PoliciaMilitar.
     * Garante que ele não seja já vinculado como Bombeiro.
     */
    @Transactional
    public PoliciaMilitar vincularUsuarioComoPolicial(Long usuarioId, PoliciaMilitar dadosPolicial) {
        Usuario u = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // 1.2: Verificar se já existe vínculo com Bombeiro (agora list.isEmpty())
        List<Bombeiro> possivelBombeiro = bombeiroRepository.findByUsuario(u);
        if (!possivelBombeiro.isEmpty()) {
            throw new IllegalArgumentException("Usuário já vinculado como Bombeiro.");
        }

        // 1.2: Definir papel correto
        if (!"POLICIAL".equalsIgnoreCase(u.getPapel())) {
            throw new IllegalArgumentException("Usuário precisa ter papel POLICIAL.");
        }

        dadosPolicial.setUsuario(u);
        return policiaMilitarRepository.save(dadosPolicial);
    }

    /**
     * Vincula um usuário existente a um registro de Bombeiro.
     * Garante que ele não seja já vinculado como Policial Militar.
     */
    @Transactional
    public Bombeiro vincularUsuarioComoBombeiro(Long usuarioId, Bombeiro dadosBombeiro) {
        Usuario u = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // 1.2: Verificar se já existe vínculo com Polícia Militar
        Optional<PoliciaMilitar> possivelPolicial = policiaMilitarRepository.findByUsuario(u);
        if (possivelPolicial.isPresent()) {
            throw new IllegalArgumentException("Usuário já vinculado como Polícia Militar.");
        }

        // 1.2: Definir papel correto
        if (!"BOMBEIRO".equalsIgnoreCase(u.getPapel())) {
            throw new IllegalArgumentException("Usuário precisa ter papel BOMBEIRO.");
        }

        dadosBombeiro.setUsuario(u);
        return bombeiroRepository.save(dadosBombeiro);
    }
}
