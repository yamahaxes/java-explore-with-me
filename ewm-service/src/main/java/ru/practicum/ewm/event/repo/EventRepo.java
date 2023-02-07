package ru.practicum.ewm.event.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.event.model.Event;

public interface EventRepo extends JpaRepository<Event, Long>,
        QuerydslPredicateExecutor<Event> {

}
