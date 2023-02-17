package ru.practicum.ewm.stats.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.HitDtoRequest;
import ru.practicum.ewm.stats.dto.HitDtoResponse;
import ru.practicum.ewm.stats.server.service.StatsServerService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
public class StatsServerController {

    private final StatsServerService statsServerService;

    @PostMapping("/hit")
    public ResponseEntity<HitDtoRequest> createHit(@RequestBody HitDtoRequest dtoRequest) {
        log.info("POST createHit(): dtoRequest={}", dtoRequest);
        return new ResponseEntity<>(statsServerService.post(dtoRequest), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<HitDtoResponse>> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                         @RequestParam(required = false) List<String> uris,
                                                         @RequestParam(defaultValue = "false") boolean unique) {

        log.info("GET getStats(): start={}, end={}, uris={}, unique={}", start, end, uris, unique);

        Optional<List<String>> urisOptional = Optional.of(uris);
        return
                urisOptional.map(strings -> new ResponseEntity<>(statsServerService.getStats(start, end, strings, unique), HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(statsServerService.getStats(start, end, unique), HttpStatus.OK));

    }

}
