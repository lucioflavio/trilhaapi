package br.gov.bnb.s533.core.exception;

import lombok.Getter;
import java.util.List;
import java.util.Map;

@Getter
public class RequisicaoInvalidaException extends RuntimeException {

    private final List<Map<String, String>> fieldErrors;

    public RequisicaoInvalidaException(String message, List<Map<String, String>> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }

    public List<Map<String, String>> getFieldErrors() {
        return fieldErrors;
    }

}
