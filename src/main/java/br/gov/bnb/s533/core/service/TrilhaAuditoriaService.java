package br.gov.bnb.s533.core.service;

import br.gov.bnb.s533.core.exception.AcessoBancoDadosException;
import br.gov.bnb.s533.core.exception.ErroInesperadoException;
import br.gov.bnb.s533.core.model.entity.Trilha;
import br.gov.bnb.s533.core.repository.TrilhaRepository;
import br.gov.bnb.s533.v1.dto.ResultadoDTO;
import br.gov.bnb.s533.v1.dto.RetornoAuditoriaDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.jms.JmsException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/***
 * Classe de serviço Trilha de Auditoria
 */
@Service
@Slf4j
public class TrilhaAuditoriaService {

    @Autowired
    private final MqAuditProducerService mqAuditProducerService;

    @Autowired
    private final TrilhaRepository trilhaRepository;

    public TrilhaAuditoriaService(MqAuditProducerService mqAuditProducerService, TrilhaRepository trilhaRepository) {
        this.mqAuditProducerService = mqAuditProducerService;
        this.trilhaRepository = trilhaRepository;
    }
    /**
     * @param trilha Objeto que será enviado para fila MQ para ser persistido na base posteriormente
     * @return ResultadoDTO - objeto com resultado da operação de delivery
     * @throws ErroInesperadoException
     */
    public ResultadoDTO enviaDadosFila(Trilha trilha) {
        try {
            log.info("Enviando objeto da trilha para fila MQ");
            mqAuditProducerService.enviaObjetoFilaMq(trilha);
        } catch (JmsException e){
            throw new ErroInesperadoException("Falha no envio de mensagem para fila", e);
        } catch(IllegalArgumentException ex){
            throw new ErroInesperadoException("Falha ao serializar payload para JSON", ex);
        }

        return ResultadoDTO.builder()
                .status(HttpStatus.CREATED.value())
                .message("Trilha enviada para fila MQ")
                .data(trilha)
                .build();
    }

    public ResultadoDTO consultarTrilhaAuditoria(LocalDate dataInicio, LocalDate dataFim, String identidadeResponsavel, String identidadeSistema, Pageable paginacao){
        List<RetornoAuditoriaDTO> lista = new ArrayList<>();

        try {
             lista = trilhaRepository.listarTrilhaAuditoria(dataInicio.atStartOfDay(), LocalDateTime.of(dataFim, LocalTime.MAX), identidadeResponsavel, identidadeSistema, paginacao);
        } catch (DataAccessException e) {
            throw new AcessoBancoDadosException(
                    "Falha de acesso à base de dados", e);
        }

        if(lista.isEmpty()){
            return ResultadoDTO.builder()
                    .status(HttpStatus.NO_CONTENT.value())
                    .message("Sem dados cadastrados")
                    .data(lista)
                    .build();
        }

        return ResultadoDTO.builder()
                .status(HttpStatus.OK.value())
                .message("Resultado da consulta")
                .data(lista)
                .build();
    }


}