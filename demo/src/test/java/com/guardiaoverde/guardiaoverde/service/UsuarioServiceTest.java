package com.guardiaoverde.guardiaoverde.service;

import com.guardiaoverde.guardiaoverde.domain.Usuario;
import com.guardiaoverde.guardiaoverde.domain.PoliciaMilitar;
import com.guardiaoverde.guardiaoverde.domain.Bombeiro;
import com.guardiaoverde.guardiaoverde.repository.UsuarioRepository;
import com.guardiaoverde.guardiaoverde.repository.PoliciaMilitarRepository;
import com.guardiaoverde.guardiaoverde.repository.BombeiroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PoliciaMilitarRepository policiaMilitarRepository;

    @Mock
    private BombeiroRepository bombeiroRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario admin;
    private Usuario naoAdmin;
    private Usuario userPolicial;
    private Usuario userBombeiro;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        admin = new Usuario("Admin Teste", "admin@exemplo.com", "senha", "ADMIN");
        admin.setId(1L);

        naoAdmin = new Usuario("Usuário Normal", "user@exemplo.com", "senha", "OPERADOR");
        naoAdmin.setId(2L);

        userPolicial = new Usuario("Policial", "policial@exemplo.com", "senha", "POLICIAL");
        userPolicial.setId(3L);

        userBombeiro = new Usuario("Bombeiro", "bombeiro@exemplo.com", "senha", "BOMBEIRO");
        userBombeiro.setId(4L);
    }

    //
    // TESTES PARA criarUsuario(...)
    //

    @Test
    void criarUsuario_DeveLancarException_QuandoExecutorNaoForAdmin() {
        Usuario novo = new Usuario("Novo", "novo@ex.com", "123", "OPERADOR");
        // executor com papel OPERADOR → deve falhar
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                usuarioService.criarUsuario(novo, naoAdmin)
        );
        assertEquals("Somente ADMIN pode criar usuários.", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void criarUsuario_DeveLancarException_QuandoEmailJaExistir() {
        Usuario novo = new Usuario("Novo", "email@ex.com", "123", "OPERADOR");
        when(usuarioRepository.existsByEmail("email@ex.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                usuarioService.criarUsuario(novo, admin)
        );
        assertEquals("Email já cadastrado.", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void criarUsuario_DeveLancarException_QuandoPapelInvalido() {
        Usuario novo = new Usuario("Novo", "valid@ex.com", "123", "INVALIDO");
        when(usuarioRepository.existsByEmail("valid@ex.com")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                usuarioService.criarUsuario(novo, admin)
        );
        assertTrue(ex.getMessage().startsWith("Papel inválido:"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void criarUsuario_DeveSalvarUsuario_QuandoDadosValidos() {
        Usuario novo = new Usuario("Novo", "novo@ex.com", "123", "OPERADOR");
        when(usuarioRepository.existsByEmail("novo@ex.com")).thenReturn(false);
        when(usuarioRepository.save(novo)).thenReturn(novo);

        Usuario salvo = usuarioService.criarUsuario(novo, admin);
        assertSame(novo, salvo);
        verify(usuarioRepository, times(1)).save(novo);
    }

    //
    // TESTES PARA atualizarUsuario(...)
    //

    @Test
    void atualizarUsuario_DeveLancarException_QuandoNaoForAdminENaoForProprioUsuario() {
        Usuario existente = new Usuario("X", "x@ex.com", "123", "OPERADOR");
        existente.setId(5L);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(existente));

        // executor é admin? não. executor id = 2 != 5 → falhar
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                usuarioService.atualizarUsuario(5L, existente, naoAdmin)
        );
        assertEquals("Você não tem permissão para atualizar este usuário.", ex.getMessage());
    }

    @Test
    void atualizarUsuario_DeveLancarException_QuandoEmailAtualizadoJaExistir() {
        Usuario existente = new Usuario("X", "x@ex.com", "123", "OPERADOR");
        existente.setId(5L);
        Usuario atualizado = new Usuario("X", "emailExistente@ex.com", "123", "OPERADOR");

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByEmail("emailExistente@ex.com")).thenReturn(true);

        // executor é admin → permissão, mas e-mail duplicado → falhar
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                usuarioService.atualizarUsuario(5L, atualizado, admin)
        );
        assertEquals("Email já cadastrado.", ex.getMessage());
    }

    @Test
    void atualizarUsuario_DeveSalvar_QuandoDadosValidos() {
        Usuario existente = new Usuario("X", "x@ex.com", "123", "OPERADOR");
        existente.setId(5L);
        Usuario atualizado = new Usuario("NovNome", "novo@ex.com", "123", "OPERADOR");
        atualizado.setId(5L);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByEmail("novo@ex.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario result = usuarioService.atualizarUsuario(5L, atualizado, admin);
        assertEquals("NovNome", result.getNome());
        assertEquals("novo@ex.com", result.getEmail());
        assertEquals("OPERADOR", result.getPapel());
    }

    //
    // TESTES PARA vincularUsuarioComoPolicial(...)
    //

    @Test
    void vincularUsuarioComoPolicial_DeveLancarException_QuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(100L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                usuarioService.vincularUsuarioComoPolicial(100L, new PoliciaMilitar())
        );
        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    void vincularUsuarioComoPolicial_DeveLancarException_QuandoUsuarioJaForBombeiro() {
        // simula que já existe um Bombeiro vinculado a este usuário
        when(usuarioRepository.findById(userPolicial.getId())).thenReturn(Optional.of(userPolicial));
        when(bombeiroRepository.findByUsuario(userPolicial)).thenReturn(List.of(new Bombeiro()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                usuarioService.vincularUsuarioComoPolicial(userPolicial.getId(), new PoliciaMilitar())
        );
        assertEquals("Usuário já vinculado como Bombeiro.", ex.getMessage());
    }

    @Test
    void vincularUsuarioComoPolicial_DeveLancarException_QuandoPapelIncorreto() {
        // usuário existe e não é bombeiro, mas possui papel diferente de POLICIAL
        when(usuarioRepository.findById(naoAdmin.getId())).thenReturn(Optional.of(naoAdmin));
        when(bombeiroRepository.findByUsuario(naoAdmin)).thenReturn(Collections.emptyList());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                usuarioService.vincularUsuarioComoPolicial(naoAdmin.getId(), new PoliciaMilitar())
        );
        assertEquals("Usuário precisa ter papel POLICIAL.", ex.getMessage());
    }

    @Test
    void vincularUsuarioComoPolicial_DeveSalvar_QuandoTudoValido() {
        PoliciaMilitar pm = new PoliciaMilitar();
        pm.setMatricula("12345");
        when(usuarioRepository.findById(userPolicial.getId())).thenReturn(Optional.of(userPolicial));
        when(bombeiroRepository.findByUsuario(userPolicial)).thenReturn(Collections.emptyList());
        when(policiaMilitarRepository.save(pm)).thenAnswer(invocation -> {
            PoliciaMilitar saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        PoliciaMilitar resultado = usuarioService.vincularUsuarioComoPolicial(userPolicial.getId(), pm);
        assertNotNull(resultado.getId());
        assertEquals(userPolicial, resultado.getUsuario());
    }

    //
    // TESTES PARA vincularUsuarioComoBombeiro(...)
    //

    @Test
    void vincularUsuarioComoBombeiro_DeveLancarException_QuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(200L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                usuarioService.vincularUsuarioComoBombeiro(200L, new Bombeiro())
        );
        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    void vincularUsuarioComoBombeiro_DeveLancarException_QuandoUsuarioJaForPolicial() {
        when(usuarioRepository.findById(userBombeiro.getId())).thenReturn(Optional.of(userBombeiro));
        // simula que já existe um Policial vinculado a este usuário
        when(policiaMilitarRepository.findByUsuario(userBombeiro))
                .thenReturn(Optional.of(new PoliciaMilitar()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                usuarioService.vincularUsuarioComoBombeiro(userBombeiro.getId(), new Bombeiro())
        );
        assertEquals("Usuário já vinculado como Polícia Militar.", ex.getMessage());
    }

    @Test
    void vincularUsuarioComoBombeiro_DeveLancarException_QuandoPapelIncorreto() {
        when(usuarioRepository.findById(naoAdmin.getId())).thenReturn(Optional.of(naoAdmin));
        when(policiaMilitarRepository.findByUsuario(naoAdmin)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                usuarioService.vincularUsuarioComoBombeiro(naoAdmin.getId(), new Bombeiro())
        );
        assertEquals("Usuário precisa ter papel BOMBEIRO.", ex.getMessage());
    }

    @Test
    void vincularUsuarioComoBombeiro_DeveSalvar_QuandoTudoValido() {
        Bombeiro b = new Bombeiro();
        b.setNome("Bombeiro Teste");

        when(usuarioRepository.findById(userBombeiro.getId())).thenReturn(Optional.of(userBombeiro));
        when(policiaMilitarRepository.findByUsuario(userBombeiro)).thenReturn(Optional.empty());
        when(bombeiroRepository.save(b)).thenAnswer(invocation -> {
            Bombeiro saved = invocation.getArgument(0);
            saved.setId(20L);
            return saved;
        });

        Bombeiro resultado = usuarioService.vincularUsuarioComoBombeiro(userBombeiro.getId(), b);
        assertNotNull(resultado.getId());
        assertEquals(userBombeiro, resultado.getUsuario());
    }
}
