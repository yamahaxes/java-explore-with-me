package ru.practicum.ewm.event.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.util.Page;

import java.util.List;
import java.util.Optional;

public interface EventRepo extends JpaRepository<Event, Long>,
        QuerydslPredicateExecutor<Event> {

    Event getEventByIdAndState(Long id, EventState state);

    List<Event> getEventByInitiatorId(Long initiator_id, Page page);

    Optional<Event> getEventByIdAndInitiatorId(Long id, Long initiator_id);

}
