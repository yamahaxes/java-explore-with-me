package ru.practicum.ewm.event.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.util.Page;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepo extends JpaRepository<Event, Long>,
        QuerydslPredicateExecutor<Event> {

    Set<Event> getEventsByIdIn(Collection<Long> id);

    Event getEventByIdAndState(Long id, EventState state);

    List<Event> getEventByInitiatorId(Long initiatorId, Page page);

    Optional<Event> getEventByIdAndInitiatorId(Long id, Long initiatorId);

    @Query("SELECT " +
            "e " +
            "FROM Event e " +
            "WHERE e.initiator.id IN (SELECT s.person.id FROM Subscription s WHERE s.subscriber.id = ?1) " +
            "   AND e.state = ru.practicum.ewm.event.dto.EventState.PUBLISHED " +
            "   AND e.eventDate >= ?2")
    List<Event> getEventsBySubscription(Long userId, LocalDateTime onDateTime);
}
