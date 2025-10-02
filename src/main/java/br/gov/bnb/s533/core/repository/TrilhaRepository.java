package br.gov.bnb.s533.core.repository;

import br.gov.bnb.s533.v1.dto.RetornoAuditoriaDTO;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrilhaRepository {

    List<RetornoAuditoriaDTO> listarTrilhaAuditoria(
            LocalDateTime dataInicialEvento,
            LocalDateTime dataFinalEvento,
            String identidadeResponsavel,
            String identidadeSistema,
            Pageable paginacao) throws DataAccessException;
    
}