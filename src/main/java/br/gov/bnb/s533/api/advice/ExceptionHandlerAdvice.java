package br.gov.bnb.s533.api.advice;

import br.gov.bnb.s533.core.exception.AcessoBancoDadosException;
import br.gov.bnb.s533.core.exception.ErroInesperadoException;
import br.gov.bnb.s533.core.exception.NegocioException;
import br.gov.bnb.s533.core.exception.RequisicaoInvalidaException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Classe para capturar exceptions customizados e transformar em resposta Http
 * com JSON
 *
 */
@RestControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {


    @ExceptionHandler(RequisicaoInvalidaException.class)
    public ProblemDetail handleDadoInvalidoException(RequisicaoInvalidaException ex, HttpServletRequest request) {
        ProblemDetail details = createProblemDetail(HttpStatus.BAD_REQUEST, "Requisição inválida", ex.getMessage(), request.getRequestURI());

        if (ex.getFieldErrors() != null) {
            details.setProperty("errors", ex.getFieldErrors());
        }

        return details;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleDadoInvalidoException(IllegalArgumentException ex, HttpServletRequest request) {
        ProblemDetail details = createProblemDetail(HttpStatus.BAD_REQUEST, "Dado inválido", ex.getMessage(), request.getRequestURI());
        return details;
    }

    @ExceptionHandler(ErroInesperadoException.class)
    public ProblemDetail handleErroInesperadoException(ErroInesperadoException ex, HttpServletRequest request) {
        ProblemDetail details = createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado", ex.getMessage(), request.getRequestURI());

        if (ex.getClass().getFields() != null) {
            details.setProperty("errors", ex.getClass().getFields());
        }

        return details;
    }

    @ExceptionHandler(NegocioException.class)
    public ProblemDetail handleNegocioException(NegocioException ex, HttpServletRequest request) {
        ProblemDetail details = createProblemDetail(HttpStatus.BAD_REQUEST, "Dados da consulta inválido", ex.getMessage(), request.getRequestURI());

        if (ex.getClass().getFields() != null) {
            details.setProperty("errors", ex.getClass().getFields());
        }

        return details;
    }

    @ExceptionHandler(AcessoBancoDadosException.class)
    public ProblemDetail handleAcessoBancoDadosException(AcessoBancoDadosException ex, HttpServletRequest request) {
        ProblemDetail details = createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Falha de conexão a Base de dados", ex.getMessage(), request.getRequestURI());

        if (ex.getClass().getFields() != null) {
            details.setProperty("errors", ex.getClass().getFields());
        }

        return details;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleErroInesperadoException(Exception ex, HttpServletRequest request) {
        ProblemDetail details = createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado", ex.getMessage(), request.getRequestURI());

        if (ex.getClass().getFields() != null) {
            details.setProperty("errors", ex.getClass().getFields());
        }

        return details;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<String> fieldErros = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.join(" - ", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        String mensagemErro = fieldErros.isEmpty() ? ex.getMessage() : fieldErros.stream().collect(Collectors.joining("; "));

        ProblemDetail problemDetail = createProblemDetail(HttpStatus.BAD_REQUEST, "Parâmetros inválidos", mensagemErro, request.getContextPath());

        return new ResponseEntity<>(problemDetail, status);
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail, String requestURI) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setType(URI.create("https://tools.ietf.org/html/rfc7807"));
        return problemDetail;
    }
}