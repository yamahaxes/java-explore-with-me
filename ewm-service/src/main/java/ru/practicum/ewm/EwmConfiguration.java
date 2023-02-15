package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.client.StatsClient;

@Configuration
public class EwmConfiguration {

    @Bean
    public StatsClient statsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        return new StatsClient(serverUrl, builder);
    }
}
