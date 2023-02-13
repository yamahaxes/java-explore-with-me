package ru.practicum.ewm.subscription.controller.pvt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.subscription.dto.SubscriptionDto;
import ru.practicum.ewm.subscription.service.SubscriptionService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/subscriptions")
@RequiredArgsConstructor
@Validated
public class SubscriptionControllerPvt {

    private final SubscriptionService subscriptionService;

    @GetMapping("/{subscriptionId}")
    @ResponseStatus(HttpStatus.OK)
    public SubscriptionDto getSubscription(@Positive @PathVariable Long userId,
                                           @Positive @PathVariable Long subscriptionId) {
        log.info("GET getSubscription(): userId={}, subscriptionId={}", userId, subscriptionId);
        return subscriptionService.getSubscription(userId, subscriptionId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto subscribe(@Positive @PathVariable Long userId,
                          @Positive @RequestParam Long personId) {
        log.info("POST subscribe(): userId={}, personId={}", userId, personId);
        return subscriptionService.subscribe(userId, personId);
    }

    @DeleteMapping("/{subscriptionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@Positive @PathVariable Long userId,
                            @Positive @PathVariable Long subscriptionId) {

        log.info("DELETE unsubscribe(): userId={}, subscriptionId={}", userId, subscriptionId);
        subscriptionService.unsubscribe(userId, subscriptionId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SubscriptionDto> getSubscriptions(@PathVariable Long userId,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET getSubscriptions(): userId={}, from={}, size={}",
                userId, from, size);
        return subscriptionService.getSubscriptions(userId, from, size);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getActualEvents(@Positive @PathVariable Long userId) {
        log.info("GET getActualEvents(): userId={}", userId);
        return subscriptionService.getActualEvents(userId);
    }
}
