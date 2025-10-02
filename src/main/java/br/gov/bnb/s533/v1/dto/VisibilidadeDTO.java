package br.gov.bnb.s533.v1.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class VisibilidadeDTO {
    @NotNull(message = "{visibilidade.matricula.not.null}")
    @Pattern(
            regexp = "^[A-Z]\\d{6}$",
            flags = {jakarta.validation.constraints.Pattern.Flag.CASE_INSENSITIVE},
            message = "{visibilidade.matricula.padrao}"
    )
    private String matricula;
    @NotNull(message = "{visibilidade.sistema.not.null}")
    @Pattern(
            regexp = "^S\\d{3}$",
            flags = {Pattern.Flag.CASE_INSENSITIVE},
            message = "{visibilidade.sistema.padrao}"
    )
    private String sistema;
}
