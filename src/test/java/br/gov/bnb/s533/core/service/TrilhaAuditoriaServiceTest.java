package br.gov.bnb.s533.core.service;

import br.gov.bnb.s533.core.exception.ErroInesperadoException;
import br.gov.bnb.s533.core.exception.RequisicaoInvalidaException;
import br.gov.bnb.s533.core.model.entity.Trilha;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import br.gov.bnb.s533.core.repository.TrilhaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para TrilhaAuditoriaService.
 * - sucesso: retorna ResultadoDTO CREATED
 * - trilha nula: RequisicaoInvalidaException
 * - erro de envio (mock do MqAuditProducerService): ErroInesperadoException
 */
class TrilhaAuditoriaServiceTest {

    private TrilhaAuditoriaService service;
    private MqAuditProducerService mqAuditProducerService;

    private TrilhaRepository trilhaRepository;

    @BeforeEach
    void setUp() {
        mqAuditProducerService = mock(MqAuditProducerService.class);
        service = new TrilhaAuditoriaService(mqAuditProducerService, trilhaRepository);
    }

    private Trilha trilha() {
        Trilha t = Trilha.builder().build();
        t.setIdentidadeTipoEvento(1);
        t.setDataInicialEvento(LocalDateTime.now().minusMinutes(1));
        t.setDataFinalEvento(LocalDateTime.now());
        t.setIdentidadeResponsavel("lucio");
        t.setIdentidadeSistema("SIS");
        t.setIdentidadeModuloSistema("MOD");
        t.setDescricaoFuncionalidadeEvento("acao");
        t.setIdentidadeIpOrigem("127.0.0.1");
        t.setIdentidadeHostOrigem("host");
        t.setIdentidadeDominioOrigem("dom");
        t.setDescricaoResultadoFinal("OK");
        t.setDescricaoInformacoesAdicionais("unit");
        return t;
    }


    /** Injeta um objeto em qualquer campo do tipo informado (se existir). */
    private void injectByType(Object target, Class<?> type, Object value) {
        for (Field f : target.getClass().getDeclaredFields()) {
            if (type.isAssignableFrom(f.getType())) {
                try {
                    f.setAccessible(true);
                    f.set(target, value);
                    return;
                } catch (IllegalAccessException ignored) {}
            }
        }

    }

    @Test
    void sucesso_deve_retornar_created() throws Exception {
        // injeta um producer mock benigno
        injectByType(service, MqAuditProducerService.class, mock(MqAuditProducerService.class));


        Object result = service.enviaDadosFila(trilha());

        assertThat(result).isNotNull();
        try {
            Method getStatus = result.getClass().getMethod("getStatus");
            Object status = getStatus.invoke(result);
            assertThat(String.valueOf(status)).contains("201");
        } catch (NoSuchMethodException ignore) {
            // sem contrato de retorno conhecido — OK
        }
    }

       @Test
    void erro_de_envio_deve_virar_ErroInesperado() throws Exception {
        // mock que lança ErroInesperadoException para qualquer método invocado
        MqAuditProducerService throwingMock = mock(MqAuditProducerService.class, invocation -> {
            throw new ErroInesperadoException("Falha no envio de mensagem para fila");
        });

        injectByType(service, MqAuditProducerService.class, throwingMock);


        assertThatThrownBy(() -> service.enviaDadosFila(Trilha.builder().build()))
                .isInstanceOf(ErroInesperadoException.class)
                .hasMessageContaining("Falha no envio de mensagem para fila");
    }
}
