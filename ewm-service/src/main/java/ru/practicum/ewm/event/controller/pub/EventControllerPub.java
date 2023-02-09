package ru.practicum.ewm.event.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.HitDtoRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventControllerPub {

    private final EventService eventService;
    private final StatsClient statsClient;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime rangeStart,
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime rangeEnd,
                                         @RequestParam(required = false) Boolean onlyAvailable,
                                         @RequestParam(required = false) Optional<EventSort> sort,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) {

        log.info("GET getEvents(): text={},\ncategories={},\npaid={},\nrangeStart={},\nrangeEnd={},\n" +
                "onlyAvailable={},\nsort={},\nfrom={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        sendStat(request);

        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@Positive @PathVariable Long id,
                                 HttpServletRequest request) {
        log.info("GET getEvent(): id={}", id);
        sendStat(request);

        return eventService.getEvent(id);
    }


    private void sendStat(HttpServletRequest request) {

        new Thread(() -> {
            HitDtoRequest dto = new HitDtoRequest();
            dto.setApp("ewm-main-service");
            dto.setIp(request.getRemoteAddr());
            dto.setTimestamp(LocalDateTime.now());
            dto.setUri(request.getRequestURI());
            try {
                ResponseEntity<Object> result = statsClient.createHit(dto);
                if (result.getStatusCode() == HttpStatus.CREATED) {
                    log.info("STAT: created hit={}, status={}", dto, result.getStatusCode());
                } else {
                    log.info("STAT: error created hit={}, status={}", dto, result.getStatusCode());
                }
            } catch (RuntimeException ex) {
                log.info("Error send state: " + ex.getMessage());
            }
        }).start();
    }
}

