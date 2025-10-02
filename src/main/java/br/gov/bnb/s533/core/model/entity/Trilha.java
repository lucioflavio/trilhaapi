package br.gov.bnb.s533.core.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
@Entity
@Table(name = "auditoria")
public class Trilha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name= "tipo_evento")
    private Integer identidadeTipoEvento;
    @Column(name= "data_inicio_evento")
    private LocalDateTime dataInicialEvento;
    @Column(name= "data_fim_evento")
    private LocalDateTime dataFinalEvento;
    @Column(name= "identidade_responsavel")
    private String identidadeResponsavel;
    @Column(name= "identidade_sistema")
    private String identidadeSistema;
    @Column(name= "modulo_sistema")
    private String identidadeModuloSistema;
    @Column(name= "descricao_evento")
    private String descricaoFuncionalidadeEvento;
    @Column(name= "ip_origem")
    private String identidadeIpOrigem;
    @Column(name= "host_origem")
    private String identidadeHostOrigem;
    @Column(name= "dominio_origem")
    private String identidadeDominioOrigem;
    @Column(name= "resultado_final")
    private String descricaoResultadoFinal;
    @Column(name= "informacoes_adicionais")
    private String descricaoInformacoesAdicionais;


}
