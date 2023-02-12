package ru.practicum.ewm.subscription.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.subscription.dto.SubscriptionDto;
import ru.practicum.ewm.subscription.model.Subscription;
import ru.practicum.ewm.subscription.repo.SubscriptionRepo;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;
import ru.practicum.ewm.util.Page;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepo repo;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public void subscribe(Long userId, Long personId) {
        User user = userService.getUserOrThrow(userId);
        User person = userService.getUserOrThrow(personId);

        Subscription subscription = new Subscription();
        subscription.setSubscriber(user);
        subscription.setPerson(person);

        repo.save(subscription);
    }


    public void unsubscribe(Long userId, Long subscriptionId) {
        User user = userService.getUserOrThrow(userId);
        Subscription subscription = repo.getSubscriptionByIdAndSubscriber(subscriptionId, user)
                .orElseThrow(() -> new NotFoundException("Subscription by id=" + subscriptionId + " and userId=" + user + " " +
                        "was not found."));

        repo.delete(subscription);
    }

    public List<SubscriptionDto> getSubscriptions(Long userId, Integer from, Integer size) {

        User user = userService.getUserOrThrow(userId);
        return repo.getSubscriptionsBySubscriber(user, new Page(from, size))
                .stream().map(s -> modelMapper.map(s, SubscriptionDto.class))
                .collect(Collectors.toList());

    }
}
