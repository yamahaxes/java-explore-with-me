package ru.practicum.ewm.subscription.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repo.EventRepo;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.subscription.dto.SubscriptionDto;
import ru.practicum.ewm.subscription.model.Subscription;
import ru.practicum.ewm.subscription.repo.SubscriptionRepo;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;
import ru.practicum.ewm.util.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepo repo;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final EventRepo eventRepo;
    private final EventMapper eventMapper;

    public SubscriptionDto subscribe(Long userId, Long personId) {

        User person = userService.getUserOrThrow(personId);
        if (!person.getPrivacySubscription()) {
            throw new ForbiddenException("The user with id=" + personId + " has disabled the ability to subscribe to him");
        }

        User user = userService.getUserOrThrow(userId);

        Subscription subscription = new Subscription();
        subscription.setSubscriber(user);
        subscription.setPerson(person);

        return modelMapper.map(repo.save(subscription), SubscriptionDto.class);
    }


    public void unsubscribe(Long userId, Long subscriptionId) {
        User user = userService.getUserOrThrow(userId);
        Subscription subscription = getOrThrow(user, subscriptionId);

        repo.delete(subscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionDto> getSubscriptions(Long userId, Integer from, Integer size) {
        User user = userService.getUserOrThrow(userId);

        return repo.getSubscriptionsBySubscriber(user, new Page(from, size))
                .stream().map(s -> modelMapper.map(s, SubscriptionDto.class))
                .collect(Collectors.toList());

    }

    public List<EventShortDto> getActualEvents(Long userId) {
        List<Event> events = eventRepo.getEventsBySubscription(userId, LocalDateTime.now());
        return eventMapper.toEventShortDtoList(events);
    }

    public SubscriptionDto getSubscription(Long userId, Long subscriptionId) {
        User user = userService.getUserOrThrow(userId);
        return modelMapper.map(getOrThrow(user, subscriptionId), SubscriptionDto.class);
    }

    public Subscription getOrThrow(User user, Long subscriptionId) {
        return repo.getSubscriptionByIdAndSubscriber(subscriptionId, user)
                .orElseThrow(() -> new NotFoundException("Subscription by id=" + subscriptionId + " and userId=" + user + " " +
                        "was not found."));
    }
}
