package br.gov.bnb.s533.core.model.mapper;

import br.gov.bnb.s533.core.model.entity.Trilha;
import br.gov.bnb.s533.v1.dto.TrilhaDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrilhaMapper {
    TrilhaDTO trilhaToTrilhaDto(Trilha trilha);
    Trilha trilhaDtoToTrilha(TrilhaDTO trilhaDTO);
}
