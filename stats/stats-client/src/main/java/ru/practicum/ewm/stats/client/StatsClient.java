package ru.practicum.ewm.stats.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.stats.dto.HitDtoRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class StatsClient extends BaseClient {

    public StatsClient(String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createHit(HitDtoRequest hitDtoRequest) {
        return post("/hit", hitDtoRequest);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start,
                                           LocalDateTime end,
                                           List<String> uri,
                                           boolean unique) {

        String path;
        Map<String, Object> parameters;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (uri != null) {
            parameters = Map.of(
                    "start", start.format(formatter),
                    "end", end.format(formatter),
                    "uri", uri,
                    "unique", unique
            );

            path = "/stats?start={start}&end={end}&uri={uri}&unique={unique}";
        } else {
            parameters = Map.of(
                    "start", start.format(formatter),
                    "end", end.format(formatter),
                    "unique", unique
            );
            path = "/stats?start={start}&end={end}&unique={unique}";
        }

        return get(path, parameters);
    }
}
