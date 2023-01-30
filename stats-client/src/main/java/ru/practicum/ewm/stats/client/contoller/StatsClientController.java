package ru.practicum.ewm.stats.client.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.HitDtoRequest;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
public class StatsClientController {

    private final StatsClient client;

    @PostMapping("/hit")
    public ResponseEntity<Object> createHit(@RequestBody HitDtoRequest dtoRequest) {
        log.info("POST createHit(): dtoRequest={}", dtoRequest);
        return client.createHit(dtoRequest);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                         @RequestParam(required = false) Optional<List<String>> uris,
                                                         @RequestParam(defaultValue = "false") boolean unique) {

        log.info("GET getStats(): start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return client.getStats(start, end, uris, unique);

    }

}
