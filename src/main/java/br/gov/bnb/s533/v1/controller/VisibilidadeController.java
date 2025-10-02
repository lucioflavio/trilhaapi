package br.gov.bnb.s533.v1.controller;

import br.gov.bnb.s533.core.exception.NegocioException;
import br.gov.bnb.s533.core.model.mapper.VisibilidadeMapper;
import br.gov.bnb.s533.core.service.VisibilidadeService;
import br.gov.bnb.s533.v1.dto.ProblemDetails;
import br.gov.bnb.s533.v1.dto.ResultadoDTO;
import br.gov.bnb.s533.v1.dto.VisibilidadeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/visibilidades")
@AllArgsConstructor
@Tag(name = "Visibilidade CRUD", description = "API de CRUD da Visibilidade da Trilha de Auditoria")
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
public class VisibilidadeController {

    @Autowired
    private VisibilidadeService visibilidadeService;

    @Autowired
    private VisibilidadeMapper visibilidadeMapper;

    @Operation(
            summary = "Cadastra Visibilidade",
            description = "Faz o cadastro da visibilidade de sistema por usuário",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = VisibilidadeDTO.class),
                            examples = @ExampleObject(name = "exemplo", value = """
                                { "matricula": "A123456", "sistema": "S533" }
                            """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            description = "Sucesso",
                            responseCode = "201",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = VisibilidadeDTO.class)))
            })
    @PostMapping
    public ResponseEntity<Object> casdastraVisibilidade(@RequestBody @Valid VisibilidadeDTO visibilidadeDTO) throws NegocioException{
        log.info("Objeto com dados para cadastrar visibilidade recebido: {}", visibilidadeDTO.toString());
        ResultadoDTO resultado = visibilidadeService.casdastraVisibilidade(visibilidadeDTO);
        log.info("Consulta realizada com sucesso: {}", resultado.getMessage());
        return new ResponseEntity<>(resultado, HttpStatus.OK);

    }

    @Operation(
            summary = "Consulta Visibilidade",
            description = "Consulta a Visibilidade de um usuário e sistema.",
            responses = {
                @ApiResponse(
                        description = "Sucesso",
                        responseCode = "200",
                        content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = VisibilidadeDTO.class))),
                @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content),
                @ApiResponse(
                        responseCode = "401",
                        description = "Falha na autenticação",
                        content = @Content(schema = @Schema(hidden = true)))
            })
    @GetMapping("/{matricula}/{sistema}")
    public ResponseEntity<VisibilidadeDTO> consultaVisibilidadeById(
            @PathVariable
            @Pattern(regexp = "^[A-Z]\\d{6}$", flags = Pattern.Flag.CASE_INSENSITIVE) String matricula,
            @PathVariable
            @Pattern(regexp = "^S\\d{3}$", flags = Pattern.Flag.CASE_INSENSITIVE) String sistema) {
        return ResponseEntity.ok(visibilidadeService.consultaVisibilidadeById(matricula, sistema));
    }

    @Operation(
            summary = "Consulta Visibilidades",
            description = "Consulta Visibilidades por filtro.",
            responses = {
                    @ApiResponse(
                            description = "Sucesso",
                            responseCode = "200",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = VisibilidadeDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Falha na autenticação",
                            content = @Content(schema = @Schema(hidden = true)))
            })
    @GetMapping
    public ResponseEntity<List<VisibilidadeDTO>> consultaVisibilidades(
            @RequestParam(required = false)
            @Pattern(regexp = "^[A-Z]\\d{6}$", flags = Pattern.Flag.CASE_INSENSITIVE,
                    message = "{visibilidade.matricula.padrao}") String matricula,
            @RequestParam(required = false)
            @Pattern(regexp = "^S\\d{3}$", flags = Pattern.Flag.CASE_INSENSITIVE,
                    message = "{visibilidade.sistema.padrao}") String sistema) {
        return ResponseEntity.ok(visibilidadeService.consultaVisibilidadeByFiltro(matricula, sistema));
    }

    @Operation(
            summary = "Altera Visibilidade",
            description = "Altera Visibilidade dos valores antigos no path, por novos valores informados no body.",
            responses = {
                    @ApiResponse(
                            description = "Alteração concluída com Sucesso",
                            responseCode = "204",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Falha na autenticação",
                            content = @Content(schema = @Schema(hidden = true)))
            })
    @PutMapping("/{matricula}/{sistema}")
    public ResponseEntity<Void> update(
            @PathVariable
            @Pattern(regexp = "^[A-Z]\\d{6}$", flags = Pattern.Flag.CASE_INSENSITIVE) String matricula,
            @PathVariable
            @Pattern(regexp = "^S\\d{3}$", flags = Pattern.Flag.CASE_INSENSITIVE) String sistema,
            @Valid @RequestBody VisibilidadeDTO body) {
        visibilidadeService.alteraVisibilidade(matricula, sistema, body);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Exclui Visibilidade",
            description = "Exclui Visibilidade por Id.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Visibilidade Removida."),
                    @ApiResponse(responseCode = "404", description = "Não encontrado",
                            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE))
            })
    @DeleteMapping("/{matricula}/{sistema}")
    public ResponseEntity<Void> delete(
            @PathVariable
            @Pattern(regexp = "^[A-Z]\\d{6}$", flags = Pattern.Flag.CASE_INSENSITIVE) String matricula,
            @PathVariable
            @Pattern(regexp = "^S\\d{3}$", flags = Pattern.Flag.CASE_INSENSITIVE) String sistema) throws NegocioException {
        visibilidadeService.delete(matricula, sistema);
        return ResponseEntity.noContent().build();
    }

}
