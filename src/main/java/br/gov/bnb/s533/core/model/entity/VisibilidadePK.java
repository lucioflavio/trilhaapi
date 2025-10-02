package br.gov.bnb.s533.core.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Data
@Getter @Setter
public class VisibilidadePK implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "matricula_usuario")
    private String matricula;

    @Column(name = "codigo_sistema")
    private String sistema;

    public VisibilidadePK(String matricula, String sistema) {
        this.matricula = matricula;
        this.sistema = sistema;
    }

    public VisibilidadePK() {}
}