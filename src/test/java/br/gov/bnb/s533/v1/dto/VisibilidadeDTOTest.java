package br.gov.bnb.s533.v1.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class VisibilidadeDTOTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static VisibilidadeDTO dto(String matricula, String sistema) {
        VisibilidadeDTO v = new VisibilidadeDTO();
        v.setMatricula(matricula);
        v.setSistema(sistema);
        return v;
    }

    @Test
    void deve_validar_quando_matricula_e_sistema_sao_validos() {
        VisibilidadeDTO v = dto("A123456", "S533"); // válidos
        Set<ConstraintViolation<VisibilidadeDTO>> violations = validator.validate(v);
        assertThat(violations).isEmpty();
    }

    @Test
    void deve_invalidar_quando_matricula_vazia() {
        VisibilidadeDTO v = dto(null, "S123");
        Set<ConstraintViolation<VisibilidadeDTO>> violations = validator.validate(v);

        assertThat(violations).anySatisfy(cv -> {
            assertThat(cv.getPropertyPath().toString()).isEqualTo("matricula");
            // Para testes de Bean Validation puros, a mensagem pode vir com o template:
            assertThat(cv.getMessageTemplate()).isEqualTo("{visibilidade.matricula.not.null}");
        });
    }

    @Test
    void deve_invalidar_quando_matricula_nao_segue_padrao() {
        VisibilidadeDTO v = dto("Z12", "S123"); // formato errado
        Set<ConstraintViolation<VisibilidadeDTO>> violations = validator.validate(v);

        assertThat(violations).anySatisfy(cv -> {
            assertThat(cv.getPropertyPath().toString()).isEqualTo("matricula");
            assertThat(cv.getMessageTemplate()).isEqualTo("{visibilidade.matricula.padrao}");
        });
    }

    @Test
    void deve_invalidar_quando_sistema_vazio() {
        VisibilidadeDTO v = dto("A123456", null);
        Set<ConstraintViolation<VisibilidadeDTO>> violations = validator.validate(v);

        assertThat(violations).anySatisfy(cv -> {
            assertThat(cv.getPropertyPath().toString()).isEqualTo("sistema");
            assertThat(cv.getMessageTemplate()).isEqualTo("{visibilidade.sistema.not.null}");
        });
    }

    @Test
    void deve_invalidar_quando_sistema_nao_segue_padrao() {
        VisibilidadeDTO v = dto("A123456", "X999"); // deveria começar com S
        Set<ConstraintViolation<VisibilidadeDTO>> violations = validator.validate(v);

        assertThat(violations).anySatisfy(cv -> {
            assertThat(cv.getPropertyPath().toString()).isEqualTo("sistema");
            assertThat(cv.getMessageTemplate()).isEqualTo("{visibilidade.sistema.padrao}");
        });
    }
}

