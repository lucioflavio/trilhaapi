package br.gov.bnb.s533.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import br.gov.bnb.s095.bnbauth.integracao.iskey.ServicoIskeyFacade;

@Configuration
public class IsKeyConfiguration {

    @Bean
    public ServicoIskeyFacade create() {
        return new ServicoIskeyFacade();
    }
}
