package br.gov.bnb.s533.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.DeliveryMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqAuditProducerService {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ibm.mq.queue.trilha.entrada}")
    private String queueName;

    /**
     * Envia objeto como JSON (TextMessage).
     * @param payload Objeto a ser enviado para Fila
     */
    public void enviaObjetoFilaMq(Object payload) throws JmsException, IllegalArgumentException{
        log.info("Fila de Entrada={}, body={}", queueName, payload);
        var message = toJson(payload);
        jmsTemplate.convertAndSend(queueName, message, configMessage());
    }

    /**
     * Envia texto já serializado.
     * @param payload Objeto a ser Serializado
     */
    private String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Falha ao serializar payload para JSON", e);
        }
    }
    /**
     * Envia texto já serializado.
     * @return MessagePostProcessor objeto Jms para ser postado
     */
    private MessagePostProcessor configMessage() {
        return message -> {
            message.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
            message.setJMSCorrelationID(UUID.randomUUID().toString());
            return message;
        };
    }
}
