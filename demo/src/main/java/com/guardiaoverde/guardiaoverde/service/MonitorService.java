package com.guardiaoverde.guardiaoverde.service;

import com.guardiaoverde.guardiaoverde.domain.*;
import com.guardiaoverde.guardiaoverde.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MonitorService {

    private final AmbienteRepository ambienteRepository;
    private final RegiaoRepository regiaoRepository;
    private final ChamadoRepository chamadoRepository;
    private final AlertaRepository alertaRepository;
    private final BombeiroRepository bombeiroRepository;
    private final RotaRepository rotaRepository;

    @Value("${threshold.umidade}")
    private Double limiarUmidade;

    @Value("${threshold.temperatura}")
    private Double limiarTemperatura;

    @Autowired
    public MonitorService(
            AmbienteRepository ambienteRepository,
            RegiaoRepository regiaoRepository,
            ChamadoRepository chamadoRepository,
            AlertaRepository alertaRepository,
            BombeiroRepository bombeiroRepository,
            RotaRepository rotaRepository
    ) {
        this.ambienteRepository = ambienteRepository;
        this.regiaoRepository = regiaoRepository;
        this.chamadoRepository = chamadoRepository;
        this.alertaRepository = alertaRepository;
        this.bombeiroRepository = bombeiroRepository;
        this.rotaRepository = rotaRepository;
    }

    @Transactional
    @Scheduled(fixedDelayString = "${monitor.periodo-ms}")
    public void verificarAmbientesCriticos() {
        List<Ambiente> ambientesCriticos =
                ambienteRepository.findByTemperaturaLessThanAndUmidadeLessThan(
                        limiarTemperatura, limiarUmidade
                );

        for (Ambiente ambiente : ambientesCriticos) {
            // 1) Encontrar região (supomos que localizacao == Regiao.nome)
            Optional<Regiao> maybeRegiao = regiaoRepository.findByNome(ambiente.getLocalizacao());
            if (maybeRegiao.isEmpty()) {
                System.out.println(
                        "MonitorService: não encontrou Regiao para localizacao = "
                                + ambiente.getLocalizacao()
                );
                continue;
            }
            Regiao regiao = maybeRegiao.get();

            // 2) Criar Chamado
            Chamado chamado = new Chamado();
            chamado.setOrigem("SISTEMA");
            chamado.setDescricao(String.format(
                    "Detecção automática: Ambiente em %s com temperatura=%.2f e umidade=%.2f",
                    ambiente.getLocalizacao(),
                    ambiente.getTemperatura(),
                    ambiente.getUmidade()
            ));
            chamado.setRegiao(regiao);
            chamado.setDataHora(LocalDateTime.now());
            chamado.setPrioridade("ALTA");
            chamadoRepository.save(chamado);

            // 3) Escolher Bombeiro (aqui, apenas o primeiro da lista)
            List<Bombeiro> todosBombeiros = bombeiroRepository.findAll();
            if (todosBombeiros.isEmpty()) {
                System.out.println("MonitorService: nenhum Bombeiro cadastrado.");
                continue;
            }
            Bombeiro bombeiroDesignado = todosBombeiros.get(0);

            // 4) Criar Alerta
            Alerta alerta = new Alerta();
            alerta.setDataHora(LocalDateTime.now());
            alerta.setNivelRisco("ALTO");
            alerta.setRiscoConfirmado(true);
            alerta.setAmbiente(ambiente);
            alerta.setResponsavel(bombeiroDesignado.getUsuario());
            alertaRepository.save(alerta);

            // 5) Buscar Rota para essa região
            Optional<Rota> maybeRota =
                    rotaRepository.findByPontoDestinoContainingIgnoreCase(regiao.getNome());
            if (maybeRota.isPresent()) {
                Rota rota = maybeRota.get();
                System.out.println("MonitorService: Rota encontrada para região "
                        + regiao.getNome()
                        + ". Origem=" + rota.getPontoPartida()
                        + ", Destino=" + rota.getPontoDestino()
                );
            } else {
                System.out.println("MonitorService: nenhuma Rota cadastrada para região "
                        + regiao.getNome());
            }
        }
    }
}
