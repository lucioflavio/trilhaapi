package br.gov.bnb.s533.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDetails {

    @Builder.Default
    private URI type = URI.create("about:blank");

    @Builder.Default
    private String title = "Unexpected error";

    @Builder.Default
    private int status = 500;

    private String detail;
    private URI instance;
    private OffsetDateTime timestamp;
    private List<FieldError> errors;


    @Data
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }
}

