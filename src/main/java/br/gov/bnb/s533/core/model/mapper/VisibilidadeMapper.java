package br.gov.bnb.s533.core.model.mapper;

import br.gov.bnb.s533.core.model.entity.Visibilidade;
import br.gov.bnb.s533.v1.dto.VisibilidadeDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VisibilidadeMapper {
    VisibilidadeDTO visibilidadeToVisibilidadeDto(Visibilidade visibilidade);
    Visibilidade visibilidadeDtoToVisibilidade(VisibilidadeDTO visibilidadeDTO);
}
