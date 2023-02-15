package ru.practicum.ewm.request.controller.pvt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {

    private final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getRequests(@Positive @PathVariable Long userId) {
        log.info("GET getRequests(): userId={}", userId);
        return requestService.getRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@Positive @PathVariable Long userId,
                                    @Positive @RequestParam Long eventId) {
        log.info("POST createRequest(): userId={}, eventId={}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto cancelRequest(@Positive @PathVariable Long userId,
                                    @Positive @PathVariable Long requestId) {
        log.info("PATCH cancelRequest(): userId={}, requestId={}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }

}
