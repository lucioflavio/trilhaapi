package br.gov.bnb.s533.v1.controller;

import br.gov.bnb.s533.core.exception.NegocioException;
import br.gov.bnb.s533.core.model.mapper.TrilhaMapper;
import br.gov.bnb.s533.core.service.TrilhaAuditoriaService;
import br.gov.bnb.s533.core.utils.Constantes;
import br.gov.bnb.s533.v1.dto.ProblemDetails;
import br.gov.bnb.s533.v1.dto.ResultadoDTO;
import br.gov.bnb.s533.v1.dto.TrilhaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


@RestController
@Slf4j
@RequestMapping("/api/v1/trilhas")
@AllArgsConstructor
@Tag(name = "Trilha de Auditoria Service", description = "API faz a trilha dos campos auditáveis na base de dados")
@ApiResponses(
        value = {
                @ApiResponse(responseCode = "404", description = "Não encontrado.", content = @Content),
                @ApiResponse(
                        responseCode = "401",
                        description = "Falha na autenticação.",
                        content = @Content(schema = @Schema(hidden = true))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Parâmetros inválidos ou não preenchidos",
                        content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
                @ApiResponse(
                        responseCode = "422",
                        description = "Dados inválidos na requisição.",
                        content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Erro inesperado.",
                        content = @Content(schema = @Schema(implementation = ProblemDetails.class)))
        })
public class TrilhaAuditoriaController {

    @Autowired
    private TrilhaAuditoriaService trilhaAuditoriaService;

    @Autowired
    private TrilhaMapper trilhaMapper;

    @Operation(
            summary = "Audita dados da operação",
            description = "Envia os dados da operação realizada para uma fila MQ",
            responses = {
                    @ApiResponse(
                            description = "Sucesso",
                            responseCode = "201",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TrilhaDTO.class)))
            })
    @PostMapping
    public ResponseEntity<Object> auditar(@RequestBody @Valid TrilhaDTO trilhaDTO) {
        log.info("Objeto com dados para auditar recebido: {}", trilhaDTO);

        ResultadoDTO resultado = trilhaAuditoriaService.enviaDadosFila(trilhaMapper.trilhaDtoToTrilha(trilhaDTO));
        log.info("Trilha enviada para fila MQ com sucesso: {}", resultado.getMessage());
        return new ResponseEntity<>(resultado, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Consulta Trilha de Auditoria",
            description = "Consulta os dados auditados disponíveis na base de dados",
            responses = {
                    @ApiResponse(
                            description = "Sucesso",
                            responseCode = "200",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Falha na autenticação",
                            content = @Content(schema = @Schema(hidden = true)))
            })
    @GetMapping
    public ResponseEntity<Object> consultarTrilhaAuditoria(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate dataFim,
            @RequestParam(required = false) String identidadeResponsavel,
            @RequestParam(required = false) String identidadeSistema,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) throws NegocioException {

        validarDatas(dataInicio, dataFim);
        Pageable paginacao = PageRequest.of(offset, limit);
        ResultadoDTO resultado = trilhaAuditoriaService.consultarTrilhaAuditoria(dataInicio, dataFim, identidadeResponsavel, identidadeSistema, paginacao);
        log.info("Consulta realizada com sucesso: {}", resultado.getMessage());
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }


    private void validarDatas(LocalDate dataInicio, LocalDate dataFim) throws NegocioException {
        if(dataInicio.isAfter(dataFim)) {
            throw new NegocioException("A data final deve ser maior que a data inicial");
        }
        long diferencaEmDias = ChronoUnit.DAYS.between(dataInicio, dataFim);
        if(diferencaEmDias > Constantes.DIFERENCA_DIAS_CONSULTA_AUDITORIA){
            throw new NegocioException("O intervalo entre as datas não deve ultrapassar 90 dias");
        }
    }


}