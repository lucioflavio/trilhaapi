package br.gov.bnb.s533.core.exception;

public class ErroInesperadoException extends RuntimeException {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -224250133826530810L;

    public ErroInesperadoException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErroInesperadoException(String message) {
        super(message);
    }

    public ErroInesperadoException(Throwable cause) {
        super(cause);
    }
}
