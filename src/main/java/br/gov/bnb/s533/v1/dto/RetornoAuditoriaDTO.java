package br.gov.bnb.s533.v1.dto;


import java.time.LocalDateTime;

public record RetornoAuditoriaDTO(
    Integer id,
    LocalDateTime dataInicioEvento,
    LocalDateTime dataFimEvento,
    String identidadeResponsavel,
    String identidadeSistema,
    String identidadeModuloSistema,
    String descricaoFuncionalidadeEvento,
    String identidadeIpOrigem,
    String identidadeHostOrigem,
    String identidadeDominioOrigem,
    Integer tipoEvento,
    String descricaoTipoEvento
){}
