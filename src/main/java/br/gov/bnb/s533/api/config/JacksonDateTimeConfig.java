package br.gov.bnb.s533.api.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer; 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
 
@Configuration
public class JacksonDateTimeConfig {
 
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
 
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
 
            JavaTimeModule module = new JavaTimeModule();
            module.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
            module.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
 
            module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
            module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
 
            builder.modules(module);
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }
}
