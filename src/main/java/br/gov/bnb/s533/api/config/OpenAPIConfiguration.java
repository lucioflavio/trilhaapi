package br.gov.bnb.s533.api.config;

import br.gov.bnb.s533.core.utils.Constantes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 *
 * Classe para configuração da documentação OpenAPI
 *
 */
@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI customOpenAPI(@Value("${info.app.version}") String appVersion) {

        OpenAPI open = new OpenAPI();

        open.info(new Info().title(Constantes.TITULO_SWAGGER).version(appVersion).description(Constantes.DESCRICAO_SWAGGER));

        open.components(new Components().addSecuritySchemes("sso_jwt",
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("sso_jwt"));
        return open;
    }

}