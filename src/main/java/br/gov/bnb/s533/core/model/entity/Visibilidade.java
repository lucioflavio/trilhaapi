package br.gov.bnb.s533.core.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "visibilidade")
public class Visibilidade {

    @EmbeddedId
    private VisibilidadePK id;

    public Visibilidade() {}

    public Visibilidade(VisibilidadePK id) {
        this.id = id;
    }
}
