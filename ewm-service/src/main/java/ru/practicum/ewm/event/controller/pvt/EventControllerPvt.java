package ru.practicum.ewm.event.controller.pvt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatus;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class EventControllerPvt {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@Positive @PathVariable Long userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET getEvents(): userId={}, from={}, size={}", userId, from, size);
        return eventService.getEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@Positive @PathVariable Long userId,
                                    @Valid @RequestBody EventNewDto dto) {

        log.info("POST createEvent(): userId={}, dto={}", userId, dto);

        return eventService.createEvent(userId, dto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@Positive @PathVariable Long userId,
                                 @Positive @PathVariable Long eventId) {
        log.info("GET getEvent(): userId={}, eventId={}", userId, eventId);

        return eventService.getEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@Positive @PathVariable Long userId,
                                    @Positive @PathVariable Long eventId,
                                    @Valid @RequestBody EventDtoUpdateUser dto) {

        return eventService.updateEvent(userId, eventId, dto);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getRequests(@Positive @PathVariable Long userId,
                                        @Positive @PathVariable Long eventId) {
        log.info("GET getRequests(): userId={}, eventId={}", userId, eventId);
        return eventService.getRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public RequestStatusUpdateResult updateRequestsStatus(@Positive @PathVariable Long userId,
                                                          @Positive @PathVariable Long eventId,
                                                          @Valid @RequestBody RequestStatusUpdate dto) {

        log.info("PATCH updateRequestsStatus(): userId={}, eventId={}, dto={}", userId, eventId, dto);

        if (dto.getStatus() != RequestStatus.CONFIRMED &&
                dto.getStatus() != RequestStatus.REJECTED) {
            throw new BadRequestException("Incorrect status.");
        }

        return eventService.updateRequestsStatus(userId, eventId, dto);
    }

}
