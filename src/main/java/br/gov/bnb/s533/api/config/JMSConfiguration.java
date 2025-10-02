package br.gov.bnb.s533.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.common.CommonConstants;
import jakarta.jms.DeliveryMode;
import jakarta.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@EnableJms
@Configuration
public class JMSConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(JMSConfiguration.class);

    @Value("${ibm.mq.host}")
    private String host;

    @Value("${ibm.mq.channel}")
    private String channel;

    @Value("${ibm.mq.port}")
    private int port;

    @Value("${ibm.mq.queueManager}")
    private String queueManager;

    @Value("${ibm.mq.receive-timeout}")
    private long receiveTimeout;

    @Value("${ibm.mq.queue.trilha.entrada}")
    private String queueName;

    private static final String APPNAME = "s533TrilhaAuditoriaServico";

    /**
     * Converte objetos Java em JSON (TextMessage).
     * Só registra se não houver outro converter no contexto.
     */
    @Bean
    @ConditionalOnMissingBean(MappingJackson2MessageConverter.class)
    public MappingJackson2MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);   // envia como TextMessage
        converter.setTypeIdPropertyName("_type");    // útil se precisar desserializar no consumidor
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    @Bean
    public MQQueueConnectionFactory mqQueueConnectionFactory() {

        MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();
        mqQueueConnectionFactory.setHostName(host);

        try {
            mqQueueConnectionFactory.setTransportType(CommonConstants.WMQ_CM_CLIENT);
            mqQueueConnectionFactory.setCCSID(1208);
            mqQueueConnectionFactory.setChannel(channel);
            mqQueueConnectionFactory.setPort(port);
            mqQueueConnectionFactory.setQueueManager(queueManager);
            mqQueueConnectionFactory.setAppName(APPNAME);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return mqQueueConnectionFactory;
    }

    UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter(
            MQQueueConnectionFactory mqQueueConnectionFactory) {

        UserCredentialsConnectionFactoryAdapter userCredentialsConnectionFactoryAdapter =
                new UserCredentialsConnectionFactoryAdapter();
        userCredentialsConnectionFactoryAdapter.setTargetConnectionFactory(mqQueueConnectionFactory);
        return userCredentialsConnectionFactoryAdapter;
    }

    @Bean
    @Primary
    public CachingConnectionFactory cachingConnectionFactory(MQQueueConnectionFactory mqQueueConnectionFactory) {

        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setTargetConnectionFactory(mqQueueConnectionFactory);
        cachingConnectionFactory.setSessionCacheSize(500);
        cachingConnectionFactory.setReconnectOnException(true);
        return cachingConnectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate(CachingConnectionFactory cachingConnectionFactory) {

        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory);
        jmsTemplate.setReceiveTimeout(receiveTimeout);
        return jmsTemplate;
    }

    /**
     * JmsTemplate dedicado para FILA,
     * - pubSubDomain=false (fila)
     * - delivery PERSISTENT
     * - destino padrão de application.properties (ibm.mq.queue.trilha.entrada)
     * - opcionalmente aplica receive-timeout se existir nas propriedades
     */
    @Bean(name = "queueJmsTemplate")
    public JmsTemplate queueJmsTemplate(CachingConnectionFactory cachingConnectionFactory,
            MappingJackson2MessageConverter messageConverter) {
        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory);
        jmsTemplate.setPubSubDomain(false);                 // fila
        jmsTemplate.setMessageConverter(messageConverter);  // Jackson -> JSON
        jmsTemplate.setDefaultDestinationName(queueName);

        // QoS explícito para garantir persistência de entrega
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);

        // Timeout opcional de receive (não afeta send, mas útil em testes/consumo)
        if (receiveTimeout > 0) {
            jmsTemplate.setReceiveTimeout(receiveTimeout);
        }

        return jmsTemplate;
    }

    @Bean
    public DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory() {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(mqQueueConnectionFactory());
        factory.setConcurrency("1-1");
        factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        factory.setSessionTransacted(true);
        return factory;
    }
}
