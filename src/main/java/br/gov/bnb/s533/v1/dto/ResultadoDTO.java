package br.gov.bnb.s533.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultadoDTO<T> {

    @Builder.Default
    private int status = 200;

    @Builder.Default
    private String message = "Success";

    private T data;
}

