package br.gov.bnb.s533.core.exception;

public class AcessoBancoDadosException extends ErroInesperadoException {

    private static final long serialVersionUID = 9136136941548020654L;

    /**
     * @param mensagemErro
     */
    public AcessoBancoDadosException(String mensagemErro) {
        super(mensagemErro);
    }

    /**
     * @param mensagemErro
     * @param causa
     */
    public AcessoBancoDadosException(String message, Exception causa) {
        super(message, causa);
    }
}
