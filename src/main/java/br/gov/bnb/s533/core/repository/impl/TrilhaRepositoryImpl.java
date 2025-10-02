package br.gov.bnb.s533.core.repository.impl;

import br.gov.bnb.s533.core.repository.TrilhaRepository;
import br.gov.bnb.s533.v1.dto.RetornoAuditoriaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TrilhaRepositoryImpl implements TrilhaRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<RetornoAuditoriaDTO> listarTrilhaAuditoria(
            LocalDateTime dataInicialEvento,
            LocalDateTime dataFinalEvento,
            String identidadeResponsavel,
            String identidadeSistema, Pageable paginacao) throws DataAccessException {

        StringBuilder sql = new StringBuilder("""
            SELECT
              a.id,
              a.data_inicio_evento                   AS data_inicio_evento,
              a.data_fim_evento                      AS data_fim_evento,
              a.identidade_responsavel               AS identidade_responsavel,
              a.identidade_sistema                   AS identidade_sistema,
              a.modulo_sistema                       AS identidade_modulo_sistema,
              a.descricao_evento                     AS descricao_funcionalidade_evento,
              a.ip_origem                            AS identidade_ip_origem,
              a.host_origem                          AS identidade_host_origem,
              a.dominio_origem                       AS identidade_dominio_origem,
              a.tipo_evento                          AS tipo_evento,
              te.descricao_evento                    AS descricao_tipo_evento
            FROM auditoria a
            JOIN tipo_evento te ON te.tipo_evento = a.tipo_evento
            WHERE 1=1
              AND a.data_inicio_evento BETWEEN :dataInicialEvento AND :dataFinalEvento
            """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("dataInicialEvento", Timestamp.valueOf(dataInicialEvento))
                .addValue("dataFinalEvento", Timestamp.valueOf((dataFinalEvento)));

        if(identidadeResponsavel != null && !identidadeResponsavel.isBlank()){
            sql.append(" AND a.identidade_responsavel = :identidadeResponsavel");
            params.addValue("identidadeResponsavel", identidadeResponsavel);
        }
        if(identidadeSistema != null && !identidadeSistema.isBlank()){
            sql.append(" AND a.identidade_sistema = :identidadeSistema");
            params.addValue("identidadeSistema", identidadeSistema);
        }

        sql.append(" ORDER BY a.data_inicio_evento ASC");

        sql.append(" LIMIT :lim OFFSET :off");
        params.addValue("lim", paginacao.getPageSize());
        params.addValue("off", paginacao.getOffset());

        List<RetornoAuditoriaDTO> resultado = jdbc.query(sql.toString(), params, (rs, i) ->
                new RetornoAuditoriaDTO(
                        rs.getInt("id"),
                        rs.getTimestamp("data_inicio_evento").toLocalDateTime(),
                        rs.getTimestamp("data_fim_evento").toLocalDateTime(),
                        rs.getString("identidade_responsavel"),
                        rs.getString("identidade_sistema"),
                        rs.getString("identidade_modulo_sistema"),
                        rs.getString("descricao_funcionalidade_evento"),
                        rs.getString("identidade_ip_origem"),
                        rs.getString("identidade_host_origem"),
                        rs.getString("identidade_dominio_origem"),
                        rs.getInt("tipo_evento"),
                        rs.getString("descricao_tipo_evento")
                )
        );

        return resultado;
    }


}
