package br.gov.bnb.s533.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TrilhaApiClientConfig {

    @Bean
    public RestTemplate trilhaApiRestTemplate(
            RestTemplateBuilder builder,
            ObjectMapper objectMapper,
            @Value("${trilhaapi.client.base-url}") String baseUrl
    ) {
        return builder
                .additionalCustomizers(restTemplate -> restTemplate.getMessageConverters().stream()
                        .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                        .map(MappingJackson2HttpMessageConverter.class::cast)
                        .forEach(converter -> converter.setObjectMapper(objectMapper)))
                .rootUri(baseUrl)
                .build();
    }
}
