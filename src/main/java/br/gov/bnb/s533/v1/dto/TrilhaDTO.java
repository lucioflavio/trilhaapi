package br.gov.bnb.s533.v1.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrilhaDTO {
    @NotNull(message = "{identidadeTipoEvento.not.null}")
    private Integer identidadeTipoEvento;
    @NotNull(message = "{dataInicialEvento.not.null}") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dataInicialEvento;
    @NotNull(message = "{dataFinalEvento.not.null}") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dataFinalEvento;
    @Size(max=50, message = "{identidadeResponsavel.size}")
    private String identidadeResponsavel;
    @Size(max=5, message = "{identidadeSistema.size}")
    private String identidadeSistema;
    @Size(max=20, message = "{identidadeModuloSistema.size}")
    private String identidadeModuloSistema;
    @Size(max=32, message = "{descricaoFuncionalidadeEvento.size}")
    private String descricaoFuncionalidadeEvento;
    @Size(max=15, message = "{identidadeIpOrigem.size}")
    private String identidadeIpOrigem;
    @Size(max=32, message = "{identidadeHostOrigem.size}")
    private String identidadeHostOrigem;
    @Size(max=50, message = "{identidadeDominioOrigem.size}")
    private String identidadeDominioOrigem;
    @Size(max=7, message = "{descricaoResultadoFinal.size}")
    private String descricaoResultadoFinal;
    @Size(max=4096, message = "{descricaoInformacoesAdicionais.size}")
    private String descricaoInformacoesAdicionais;
}
