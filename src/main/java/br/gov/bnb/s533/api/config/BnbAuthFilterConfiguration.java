package br.gov.bnb.s533.api.config;

import java.io.IOException;
import java.net.URL;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;
import br.gov.bnb.s095.bnbauth.filter.BnbAuthFilter;

@Configuration
@Profile("!test")
public class BnbAuthFilterConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(BnbAuthFilterConfiguration.class);

    @Autowired
    ResourceLoader resourceLoader;

    @Value("${keycloak.filepath}")
    private String keycloakFilepath;

    @Value("${iskey.url}")
    private String iskeyEndpoint;

    @Value("${iskey.sigla.sistema}")
    private String iskeySiglaSistema;

    private static final String URL_RECURSO_CLIENT_JSON = "url/keycloak";
    private static final String REF_URL_ISKEY = "url/s267_iskey";
    private static final String MSG_ERRO_ARQUIVO_JSON_NAO_ENCONTRADO = "As variáveis de ambiente não foram configuradas. Favor verificar template no OCP.";

    @Bean
    public ResourceLoader resourceLoader() {
        return new DefaultResourceLoader();
    }

    @Bean
    public FilterRegistrationBean<BnbAuthFilter> filterRegistrationBean() {

        this.criarRecursoJNDIRelativoURL();

        FilterRegistrationBean<BnbAuthFilter> filterRegistration = new FilterRegistrationBean<BnbAuthFilter>();
        filterRegistration.setFilter(new BnbAuthFilter());
        filterRegistration.addInitParameter("bnbauth.config.jndi", URL_RECURSO_CLIENT_JSON);
        filterRegistration.addInitParameter("iskey.endpoint.jndi", REF_URL_ISKEY);
        filterRegistration.addInitParameter("iskey.siglasistema", iskeySiglaSistema);
        filterRegistration.addUrlPatterns("/api/v1/*");

        LOGGER.info("Filtro de segurança BNB-Auth criado.");
        return filterRegistration;
    }

    /**
     * Necessário para compatibilidade com a biblioteca BNB Auth
     */
    private void criarRecursoJNDIRelativoURL() {
        try {
            LOGGER.info(
                    "Criando recurso contexto JNDI para o recurso de URL referente ao client json para a biblioteca BNB Auth.");
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");

            Context context = new InitialContext();
            context.createSubcontext("url");

            context.bind(REF_URL_ISKEY, new URL(iskeyEndpoint));
            context.bind(URL_RECURSO_CLIENT_JSON, this.carregarClientJson());

            LOGGER.info("URL do caminho do arquivo JSON vinculado ao contexto.");
        } catch (Exception e) {
            LOGGER.error("Impossível criar binding para a BNBAuth. {}", e.getMessage());
        }
    }

    private URL carregarClientJson() throws IOException {
        if (!StringUtils.hasLength(keycloakFilepath)) {
            LOGGER.error(MSG_ERRO_ARQUIVO_JSON_NAO_ENCONTRADO);
            throw new IllegalArgumentException(MSG_ERRO_ARQUIVO_JSON_NAO_ENCONTRADO);
        }

        return resourceLoader.getResource(keycloakFilepath).getURL();
    }
}