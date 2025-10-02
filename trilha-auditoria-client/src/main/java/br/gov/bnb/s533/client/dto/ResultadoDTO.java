package br.gov.bnb.s533.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
