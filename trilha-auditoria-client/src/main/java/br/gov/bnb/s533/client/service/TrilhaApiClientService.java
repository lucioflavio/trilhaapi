package br.gov.bnb.s533.client.service;

import br.gov.bnb.s533.client.dto.ResultadoDTO;
import br.gov.bnb.s533.client.dto.RetornoAuditoriaDTO;
import br.gov.bnb.s533.client.dto.TrilhaDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TrilhaApiClientService {

    private final RestTemplate trilhaApiRestTemplate;

    public TrilhaApiClientService(RestTemplate trilhaApiRestTemplate) {
        this.trilhaApiRestTemplate = trilhaApiRestTemplate;
    }

    public ResponseEntity<ResultadoDTO<TrilhaDTO>> enviarTrilha(TrilhaDTO request) {
        Assert.notNull(request, "O corpo da requisição não pode ser nulo");
        try {
            return trilhaApiRestTemplate.exchange(
                    "/api/v1/trilhas",
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<>() {}
            );
        } catch (RestClientResponseException ex) {
            throw buildResponseStatusException(ex);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao se comunicar com a Trilha API",
                    ex
            );
        }
    }

    public ResponseEntity<ResultadoDTO<List<RetornoAuditoriaDTO>>> consultarTrilhas(
            LocalDate dataInicio,
            LocalDate dataFim,
            String identidadeResponsavel,
            String identidadeSistema,
            int limit,
            int offset
    ) {
        Assert.notNull(dataInicio, "A data inicial é obrigatória");
        Assert.notNull(dataFim, "A data final é obrigatória");

        URI uri = UriComponentsBuilder.fromPath("/api/v1/trilhas")
                .queryParam("dataInicio", dataInicio)
                .queryParam("dataFim", dataFim)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .queryParamIfPresent("identidadeResponsavel", Optional.ofNullable(identidadeResponsavel))
                .queryParamIfPresent("identidadeSistema", Optional.ofNullable(identidadeSistema))
                .build(true)
                .toUri();

        try {
            return trilhaApiRestTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
        } catch (RestClientResponseException ex) {
            throw buildResponseStatusException(ex);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao se comunicar com a Trilha API",
                    ex
            );
        }
    }

    private ResponseStatusException buildResponseStatusException(RestClientResponseException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseStatusException(status, ex.getResponseBodyAsString(), ex);
    }
}
