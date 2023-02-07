package ru.practicum.ewm.event.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.controller.pub.EventSort;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repo.EventRepo;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.repo.UserRepo;
import ru.practicum.ewm.util.EwmUtils;
import ru.practicum.ewm.util.Page;
import ru.practicum.ewm.util.QPredicates;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepo eventRepo;
    private final EventMapper eventMapper;
    private final UserRepo userRepo;

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

    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        checkUser(userId);

        return eventMapper.toEventShortDtoList(
                eventRepo.getEventByInitiatorId(userId, new Page(from, size))
        );
    }

    public EventFullDto createEvent(Long userId, EventNewDto dto) {
        checkUser(userId);

        Event event = eventMapper.toModel(dto);

        return eventMapper.toEventFullDto(event);
    }

    public EventFullDto getEvent(Long userId, Long eventId) {

        Optional<Event> optionalEvent = eventRepo.getEventByIdAndInitiatorId(userId, eventId);
        if (optionalEvent.isEmpty()) {
            throw new NotFoundException("Event by id=" + eventId + " and userId=" + userId + " was not found.");
        }

        return eventMapper.toEventFullDto(
            optionalEvent.get()
        );
    }

    public EventFullDto updateEvent(Long userId, Long eventId, EventDtoUpdateUser dto) {

        Optional<Event> optionalEvent = eventRepo.getEventByIdAndInitiatorId(userId, eventId);
        if (optionalEvent.isEmpty()) {
            throw new NotFoundException("Event by id=" + eventId + " and userId=" + userId + " was not found.");
        }

        Event eventTarget = optionalEvent.get();
        if (eventTarget.getState() == EventState.PUBLISHED) {
            throw new ForbiddenException("Event by id=" + eventId + " already published.");
        }

        Event src = eventMapper.toModel(dto);
        EwmUtils.copyNotNullProperties(src, eventTarget);

        return eventMapper.toEventFullDto(
                eventRepo.save(eventTarget)
        );
    }

    private void checkUser(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("User by id=" + userId + " was not found.");
        }
    }

    private void checkEvent(Long eventId) {
        if(!eventRepo.existsById(eventId)) {
            throw new NotFoundException("Event by id=" + eventId + " was not found.");
        }
    }
}
