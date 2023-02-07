package ru.practicum.ewm;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.client.StatsClient;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Configuration
public class EwmConfiguration {

    @Bean
    public StatsClient statsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        return new StatsClient(serverUrl, builder);
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE);

        return mapper;
    }
}
