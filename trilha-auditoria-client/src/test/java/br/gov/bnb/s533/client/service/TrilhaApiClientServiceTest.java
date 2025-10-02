package br.gov.bnb.s533.client.service;

import br.gov.bnb.s533.client.dto.ResultadoDTO;
import br.gov.bnb.s533.client.dto.RetornoAuditoriaDTO;
import br.gov.bnb.s533.client.dto.TrilhaDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrilhaApiClientServiceTest {

    private TrilhaApiClientService service;
    private MockRestServiceServer server;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri("http://localhost:8080")
                .additionalCustomizers(template -> template.getMessageConverters().stream()
                        .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                        .map(MappingJackson2HttpMessageConverter.class::cast)
                        .forEach(converter -> converter.setObjectMapper(this.objectMapper)))
                .build();
        this.server = MockRestServiceServer.bindTo(restTemplate).build();
        this.service = new TrilhaApiClientService(restTemplate);
    }

    @Test
    void deveEnviarTrilhaComSucesso() throws JsonProcessingException {
        TrilhaDTO trilhaDTO = TrilhaDTO.builder()
                .identidadeTipoEvento(1)
                .dataInicialEvento(LocalDateTime.of(2024, 1, 1, 10, 0))
                .dataFinalEvento(LocalDateTime.of(2024, 1, 1, 10, 5))
                .identidadeResponsavel("usuario.test")
                .identidadeSistema("S533")
                .build();

        ResultadoDTO<TrilhaDTO> resultado = ResultadoDTO.<TrilhaDTO>builder()
                .status(HttpStatus.CREATED.value())
                .message("Trilha enviada para fila MQ")
                .data(trilhaDTO)
                .build();

        server.expect(MockRestRequestMatchers.requestTo("http://localhost:8080/api/v1/trilhas"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andExpect(MockRestRequestMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(resultado)));

        ResponseEntity<ResultadoDTO<TrilhaDTO>> resposta = service.enviarTrilha(trilhaDTO);

        server.verify();
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().getMessage()).isEqualTo("Trilha enviada para fila MQ");
    }

    @Test
    void deveConsultarTrilhasComSucesso() throws JsonProcessingException {
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 1, 10);

        List<RetornoAuditoriaDTO> dados = List.of(
                new RetornoAuditoriaDTO(
                        10,
                        LocalDateTime.of(2024, 1, 1, 10, 0),
                        LocalDateTime.of(2024, 1, 1, 10, 5),
                        "usuario.test",
                        "S533",
                        "MOD",
                        "Consulta",
                        "127.0.0.1",
                        "localhost",
                        "bnb.gov",
                        1,
                        "Consulta"
                )
        );

        ResultadoDTO<List<RetornoAuditoriaDTO>> resultado = ResultadoDTO.<List<RetornoAuditoriaDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Resultado da consulta")
                .data(dados)
                .build();

        server.expect(MockRestRequestMatchers.requestTo("http://localhost:8080/api/v1/trilhas?dataInicio=2024-01-01&dataFim=2024-01-10&limit=10&offset=0"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(objectMapper.writeValueAsString(resultado), MediaType.APPLICATION_JSON));

        ResponseEntity<ResultadoDTO<List<RetornoAuditoriaDTO>>> resposta = service.consultarTrilhas(
                dataInicio,
                dataFim,
                null,
                null,
                10,
                0
        );

        server.verify();
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().getData()).hasSize(1);
    }

    @Test
    void devePropagarErroQuandoApiRetornaFalha() {
        TrilhaDTO trilhaDTO = TrilhaDTO.builder()
                .identidadeTipoEvento(1)
                .dataInicialEvento(LocalDateTime.of(2024, 1, 1, 10, 0))
                .dataFinalEvento(LocalDateTime.of(2024, 1, 1, 10, 5))
                .build();

        server.expect(MockRestRequestMatchers.requestTo("http://localhost:8080/api/v1/trilhas"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\":\"Dados inválidos\"}"));

        assertThatThrownBy(() -> service.enviarTrilha(trilhaDTO))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(throwable -> assertThat(((ResponseStatusException) throwable).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST));
    }
}
