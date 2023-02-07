package ru.practicum.ewm.event.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.controller.pub.EventSort;
import ru.practicum.ewm.event.dto.EventDtoUpdateAdmin;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repo.EventRepo;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.util.EwmUtils;
import ru.practicum.ewm.util.Page;
import ru.practicum.ewm.util.QPredicates;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepo eventRepo;
    private final EventMapper eventMapper;

    public List<EventFullDto> getEvents(List<Long> users,
                                        List<EventState> states,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Integer from,
                                        Integer size) {

        QPredicates predicates = QPredicates.builder()
                .add(users, QEvent.event.initiator.id::in)
                .add(states, QEvent.event.state::in)
                .add(categories, QEvent.event.category.id::in)
                .add(rangeStart, QEvent.event.eventDate::goe)
                .add(rangeEnd, QEvent.event.eventDate::loe);

        List<Event> events = eventRepo.findAll(predicates.buildAnd(), new Page(from, size)).toList();

        return eventMapper.toEventFullDtoList(events);
    }

    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         EventSort sort,
                                         Integer from,
                                         Integer size) {

        QPredicates predicatesOr = QPredicates.builder()
                .add(text, QEvent.event.description::containsIgnoreCase)
                .add(text, QEvent.event.annotation::containsIgnoreCase);

        QPredicates predicatesAnd = QPredicates.builder()
                .add(categories, QEvent.event.category.id::in)
                .add(paid, QEvent.event.paid::eq)
                .add(rangeStart, QEvent.event.eventDate::goe)
                .add(rangeEnd, QEvent.event.eventDate::loe)
                .add(onlyAvailable, aBoolean -> {
                    if (aBoolean) {
                        return QEvent.event.confirmedRequests.loe(QEvent.event.participantLimit);
                    }
                    return Expressions.TRUE;
                })
                .add(EventState.PUBLISHED, QEvent.event.state::eq);

        if (rangeStart == null && rangeEnd == null) {
            predicatesAnd.add(LocalDateTime.now(), QEvent.event.eventDate::gt);
        }

        List<Predicate> predicatesAll = List.of(predicatesOr.buildOr(), predicatesAnd.buildAnd());
        Predicate predicate = ExpressionUtils.allOf(predicatesAll);

        Page page;
        if (sort.equals(EventSort.VIEWS)) {
            page = new Page(from, size, Sort.by("views"));
        } else if (sort.equals(EventSort.EVENT_DATE)) {
            page = new Page(from, size, Sort.by("eventDate"));
        } else {
            page = new Page(from, size);
        }

        return eventMapper.toEventShortDtoList(
                eventRepo.findAll(Objects.requireNonNull(predicate), page).toList()
        );

    }

    public EventFullDto updateEvent(Long eventId, EventDtoUpdateAdmin dto) {
        checkEvent(eventId);

        Event entityTarget = eventRepo.getReferenceById(eventId);
        Event src = eventMapper.toModel(dto);

        EwmUtils.copyNotNullProperties(src, entityTarget);

        return eventMapper.toEventFullDto(
                eventRepo.save(entityTarget)
        );
    }

    public EventFullDto getEvent(Long id) {
        checkEvent(id);

        return eventMapper.toEventFullDto(eventRepo.getEventByIdAndState(id, EventState.PUBLISHED));
    }

    private void checkEvent(Long eventId) {
        if(!eventRepo.existsById(eventId)) {
            throw new NotFoundException("Event by id=" + eventId + " was not found.");
        }
    }
}
