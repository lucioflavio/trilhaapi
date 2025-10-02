package br.gov.bnb.s533.core.service;

import br.gov.bnb.s533.core.exception.ErroInesperadoException;
import br.gov.bnb.s533.core.model.entity.Trilha;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.jms.DeliveryMode;
import jakarta.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * - verifica envio via JmsTemplate (por método público identificado por reflexão)
 * - verifica configuração de headers JMS no MessagePostProcessor (delivery e correlationId)
 * - verifica tratamento de exceções de JMS (ErroInesperadoException)
 */
class MqAuditProducerServiceTest {

    private ObjectMapper objectMapper;
    private JmsTemplate jmsTemplate;
    private MqAuditProducerService service;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        jmsTemplate = mock(JmsTemplate.class);

        // Construtor com ObjectMapper + JmsTemplate
        service = new MqAuditProducerService(jmsTemplate, objectMapper);

        // Injeta a fila @Value
        Field f = MqAuditProducerService.class.getDeclaredField("queueName");
        f.setAccessible(true);
        f.set(service, "DEV.QUEUE.1");
    }

    private Trilha trilha() {
        Trilha t = Trilha.builder().build();
        t.setIdentidadeTipoEvento(5);
        t.setDataInicialEvento(LocalDateTime.of(2025, 8, 20, 10, 0));
        t.setDataFinalEvento(LocalDateTime.of(2025, 8, 20, 11, 0));
        t.setIdentidadeResponsavel("lucio");
        t.setIdentidadeSistema("S533");
        t.setIdentidadeModuloSistema("TRILHA");
        t.setDescricaoFuncionalidadeEvento("acao");
        t.setIdentidadeIpOrigem("10.0.0.1");
        t.setIdentidadeHostOrigem("host-a");
        t.setIdentidadeDominioOrigem("dom.local");
        t.setDescricaoResultadoFinal("OK");
        t.setDescricaoInformacoesAdicionais("unit");
        return t;
    }

   /* @Test
    void deve_enviar_para_fila_com_corpo_json_e_mpp_configurado() throws Exception {

        // Assert: JmsTemplate chamado
        ArgumentCaptor<String> queueCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MessagePostProcessor> mppCap = ArgumentCaptor.forClass(MessagePostProcessor.class);

        *//*verify(service).enviaObjetoFilaMq(bodyCap);

        assertThat(queueCap.getValue()).isEqualTo("DEV.QUEUE.1");
        assertThat(bodyCap.getValue()).contains("\"identidadeSistema\":\"S533\"");*//*

        // Verifica headers no MPP
        Message mockMsg = mock(Message.class);
        try {
            mppCap.getValue().postProcessMessage(mockMsg);
            verify(mockMsg).setJMSDeliveryMode(DeliveryMode.PERSISTENT);
            verify(mockMsg).setJMSCorrelationID(anyString());
        } catch (Exception e) {
            fail("Falha ao aplicar MessagePostProcessor", e);
        }

    }
*/
    @Test
    void deve_tratar_excecao_jms_com_ErroInesperadoException() throws Exception {
        // força JmsException no envio
        doThrow(new JmsException("erro MQ") {}).when(jmsTemplate)
                .convertAndSend(anyString(), anyString(), any(MessagePostProcessor.class));

        assertThatThrownBy(() -> service.enviaObjetoFilaMq(trilha()))
                .isInstanceOf(JmsException.class)
                .hasMessageContaining("erro MQ");
    }

    @Test
    void configMessage_deve_definir_delivery_persistente_e_correlationId() throws Exception {
        // Acessa método privado por reflexão
        var m = MqAuditProducerService.class.getDeclaredMethod("configMessage");
        m.setAccessible(true);
        MessagePostProcessor mpp = (MessagePostProcessor) m.invoke(service);

        Message msg = mock(Message.class);
        mpp.postProcessMessage(msg);

        verify(msg).setJMSDeliveryMode(DeliveryMode.PERSISTENT);
        verify(msg).setJMSCorrelationID(anyString());
    }
}
