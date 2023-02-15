package ru.practicum.ewm.event.controller.adm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventDtoUpdateAdmin;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class EventControllerAdm {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<EventState> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime rangeStart,
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {

        log.info("GET getEvents(): users={},\nstates={},\ncategories={},\nrangeStart={},\nrangeEnd={},\nfrom={},\nsize={}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        return eventService.getEvents(users, states, categories,
                rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@Positive @PathVariable Long eventId,
                                    @RequestBody EventDtoUpdateAdmin dto) {

        log.info("PATCH updateEvent(): eventId={}, dto={}", eventId, dto);

        return eventService.updateEvent(eventId, dto);
    }
}
