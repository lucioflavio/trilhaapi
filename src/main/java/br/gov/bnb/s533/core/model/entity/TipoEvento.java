package br.gov.bnb.s533.core.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tipo_evento")
@Data
public class TipoEvento {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "tipo_evento")
    private Integer tipoEvento;

    @Column(name = "descricao_evento")
    private String descricao;


}
