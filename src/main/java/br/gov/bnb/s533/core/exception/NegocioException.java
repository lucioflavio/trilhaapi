package br.gov.bnb.s533.core.exception;

public class NegocioException extends Exception{

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 5196502210510077800L;

    public NegocioException(String message, Throwable cause) {
        super(message, cause);
    }

    public NegocioException(String message) {
        super(message);
    }
}
