package Layer.NewStudentManagement.Config;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${client.superadmin.base-url}")
    private String clientSuperAdminBaseUrl;

    @Bean
    public WebClient webClient(@Value("${client.superadmin.base-url}") String baseUrl) {
        ObjectMapper mapper = new ObjectMapper();

        // Increase allowed read nesting depth to 3000 to handle the huge item
        StreamReadConstraints constraints = StreamReadConstraints.builder()
                .maxNestingDepth(3000)
                .build();
        mapper.getFactory().setStreamReadConstraints(constraints);

        // Inject custom mapper into WebClient's codecs
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(config -> config.defaultCodecs()
                        .jackson2JsonDecoder(new Jackson2JsonDecoder(mapper)))
                .build();

        return WebClient.builder()
                .exchangeStrategies(strategies)
                .baseUrl(baseUrl)
                .build();
    }
}

