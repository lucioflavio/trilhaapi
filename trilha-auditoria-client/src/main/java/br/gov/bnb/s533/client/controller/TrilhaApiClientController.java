package br.gov.bnb.s533.client.controller;

import br.gov.bnb.s533.client.dto.ResultadoDTO;
import br.gov.bnb.s533.client.dto.RetornoAuditoriaDTO;
import br.gov.bnb.s533.client.dto.TrilhaDTO;
import br.gov.bnb.s533.client.service.TrilhaApiClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/client/trilhas")
@Validated
@Tag(name = "Cliente Trilha de Auditoria", description = "Consome os endpoints expostos na Trilha API")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "500", description = "Erro inesperado", content = @Content(schema = @Schema(hidden = true)))
})
public class TrilhaApiClientController {

    private final TrilhaApiClientService trilhaApiClientService;

    public TrilhaApiClientController(TrilhaApiClientService trilhaApiClientService) {
        this.trilhaApiClientService = trilhaApiClientService;
    }

    @Operation(summary = "Encaminha uma trilha de auditoria para a API remota")
    @PostMapping
    public ResponseEntity<ResultadoDTO<TrilhaDTO>> enviarTrilha(@RequestBody @Valid TrilhaDTO trilhaDTO) {
        ResponseEntity<ResultadoDTO<TrilhaDTO>> resposta = trilhaApiClientService.enviarTrilha(trilhaDTO);
        HttpStatus status = resposta.getStatusCode();
        return ResponseEntity.status(status).body(resposta.getBody());
    }

    @Operation(summary = "Consulta as trilhas de auditoria na API remota")
    @GetMapping
    public ResponseEntity<ResultadoDTO<List<RetornoAuditoriaDTO>>> consultarTrilhas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String identidadeResponsavel,
            @RequestParam(required = false) String identidadeSistema,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(defaultValue = "0") @Min(0) int offset
    ) {
        ResponseEntity<ResultadoDTO<List<RetornoAuditoriaDTO>>> resposta = trilhaApiClientService.consultarTrilhas(
                dataInicio,
                dataFim,
                identidadeResponsavel,
                identidadeSistema,
                limit,
                offset
        );
        HttpStatus status = resposta.getStatusCode();
        return ResponseEntity.status(status).body(resposta.getBody());
    }
}
