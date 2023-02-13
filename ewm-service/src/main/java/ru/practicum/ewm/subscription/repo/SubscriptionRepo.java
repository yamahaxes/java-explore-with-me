package ru.practicum.ewm.subscription.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.subscription.model.Subscription;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepo extends JpaRepository<Subscription, Long> {

    Optional<Subscription> getSubscriptionByIdAndSubscriber(Long id, User subscriber);

    List<Subscription>  getSubscriptionsBySubscriber(User subscriber, Pageable page);

}
