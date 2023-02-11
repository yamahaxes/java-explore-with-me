package ru.practicum.ewm.event.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.controller.pub.EventSort;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repo.EventRepo;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatus;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repo.RequestRepo;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.HitDtoRequest;
import ru.practicum.ewm.user.repo.UserRepo;
import ru.practicum.ewm.util.EwmUtils;
import ru.practicum.ewm.util.Page;
import ru.practicum.ewm.util.QPredicates;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepo eventRepo;
    private final EventMapper eventMapper;
    private final UserRepo userRepo;
    private final RequestRepo requestRepo;
    private final RequestMapper requestMapper;
    private final StatsClient statsClient;

    @Transactional(readOnly = true)
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

        Page page = new Page(from, size);
        Predicate predicate = predicates.buildAnd();

        return eventMapper.toEventFullDtoList(
                (predicate == null)
                        ? eventRepo.findAll(page).toList()
                        : eventRepo.findAll(predicate, page).toList()
        );
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         Optional<EventSort> sortOptional,
                                         Integer from,
                                         Integer size,
                                         HttpServletRequest request) {

        sendStat(request);

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
                    return Expressions.asBoolean(true).isTrue();
                })
                .add(EventState.PUBLISHED, QEvent.event.state::eq);

        if (rangeStart == null && rangeEnd == null) {
            predicatesAnd.add(LocalDateTime.now(), QEvent.event.eventDate::gt);
        }

        List<Predicate> predicatesAll = List.of(predicatesOr.buildOr(), predicatesAnd.buildAnd());
        Predicate predicate = ExpressionUtils.allOf(predicatesAll);


        Sort sort = Sort.unsorted();
        if (sortOptional.isPresent()) {
            switch (sortOptional.get()) {
                case EVENT_DATE:
                    sort = Sort.by("eventDate"); break;
                case VIEWS:
                    sort = Sort.by("views"); break;
            }
        }

        Page page = new Page(from, size, sort);

        return eventMapper.toEventShortDtoList(
                (predicate == null)
                        ? eventRepo.findAll(page).toList()
                        : eventRepo.findAll(predicate, page).toList()
        );

    }

    public EventFullDto updateEvent(Long eventId, EventDtoUpdateAdmin dto) {
        validateEventTime(dto.getEventDate(), 1);
        checkEvent(eventId);

        Event entityTarget = eventRepo.getReferenceById(eventId);
        Event src = eventMapper.toModel(dto);

        EwmUtils.copyNotNullProperties(src, entityTarget);

        doEventAction(entityTarget, dto.getStateAction());

        return eventMapper.toEventFullDto(
                eventRepo.save(entityTarget)
        );
    }

    @Transactional(readOnly = true)
    public EventFullDto getEvent(Long id, HttpServletRequest request) {
        sendStat(request);
        checkEvent(id);

        return eventMapper.toEventFullDto(eventRepo.getEventByIdAndState(id, EventState.PUBLISHED));
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        checkUser(userId);

        return eventMapper.toEventShortDtoList(
                eventRepo.getEventByInitiatorId(userId, new Page(from, size))
        );
    }

    public EventFullDto createEvent(Long userId, EventNewDto dto) {
        validateEventTime(dto.getEventDate(), 2);
        checkUser(userId);

        Event event = eventMapper.toModel(dto);

        event.setInitiator(userRepo.getReferenceById(userId));
        event.setState(EventState.PENDING);
        event.setViews(0);

        return eventMapper.toEventFullDto(eventRepo.save(event));
    }

    @Transactional(readOnly = true)
    public EventFullDto getEvent(Long userId, Long eventId) {

        Optional<Event> optionalEvent = eventRepo.getEventByIdAndInitiatorId(eventId, userId);

        return eventMapper.toEventFullDto(
            optionalEvent
                    .orElseThrow(() -> new NotFoundException("Event by id=" + eventId + " and userId=" + userId + " was not found."))
        );
    }

    public EventFullDto updateEvent(Long userId, Long eventId, EventDtoUpdateUser dto) {

        if (dto.getEventDate() != null) {
            validateEventTime(dto.getEventDate(), 2);
        }

        Optional<Event> optionalEvent = eventRepo.getEventByIdAndInitiatorId(eventId, userId);
        Event eventTarget = optionalEvent
                .orElseThrow(() -> new NotFoundException("Event by id=" + eventId + " and userId=" + userId + " was not found."));

        if (eventTarget.getState() == EventState.PUBLISHED) {
            throw new ForbiddenException("Event by id=" + eventId + " already published.");
        }

        Event src = eventMapper.toModel(dto);
        EwmUtils.copyNotNullProperties(src, eventTarget);

        doEventAction(eventTarget, dto.getStateAction());

        return eventMapper.toEventFullDto(
                eventRepo.save(eventTarget)
        );
    }

    @Transactional(readOnly = true)
    public List<RequestDto> getRequests(Long userId, Long eventId) {
        return requestMapper.toDtoList(
                requestRepo.getRequestsByEventIdAndEventInitiatorId(eventId, userId)
        );
    }

    public RequestStatusUpdateResult updateRequestsStatus(Long userId, Long eventId, RequestStatusUpdate dto) {

        checkUser(userId);
        checkEvent(eventId);

        Event event = eventRepo.getEventByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event by id=" + eventId + " was not found."));

        RequestStatusUpdateResult requestStatusUpdateResult = new RequestStatusUpdateResult();

        if (!event.getRequestModeration()
                || event.getParticipantLimit() == 0) {
            return requestStatusUpdateResult;
        }

        List<Request> requests = requestRepo.getRequestsByEventIdAndEventInitiatorIdAndIdIn(eventId, userId, dto.getRequestIds());
        requests.forEach(request -> {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ForbiddenException("Request with id=" + request.getId() + " have not status PENDING.");
            }

            request.setStatus(dto.getStatus());
            if (dto.getStatus() == RequestStatus.CONFIRMED) {
                event.incConfirmedRequests();

                if (event.limitExhausted()) {
                    throw new ForbiddenException("Request limit exceeded");
                }
            }
         });

        requestRepo.saveAll(requests);
        eventRepo.save(event);

        if (dto.getStatus().equals(RequestStatus.CONFIRMED)) {
            requestStatusUpdateResult.setConfirmedRequests(
                    requestMapper.toDtoList(requestRepo.getRequestsByEventIdAndEventInitiatorIdAndStatus(eventId, userId, RequestStatus.CONFIRMED))
            );
        } else if (dto.getStatus().equals(RequestStatus.REJECTED)) {
            requestStatusUpdateResult.setRejectedRequests(
                    requestMapper.toDtoList(requestRepo.getRequestsByEventIdAndEventInitiatorIdAndStatus(eventId, userId, RequestStatus.REJECTED))
            );
        }

        return requestStatusUpdateResult;
    }

    private void doEventAction(Event entityTarget, EventStateAction stateAction) {

        if (stateAction == EventStateAction.PUBLISH_EVENT) {
            if (entityTarget.getState() == EventState.PUBLISHED) {
                throw new ForbiddenException("Event already published.");
            } else if (entityTarget.getState() == EventState.CANCELED) {
                throw new ForbiddenException("Event canceled.");
            }

            entityTarget.setState(EventState.PUBLISHED);
        } else if (stateAction == EventStateAction.REJECT_EVENT) {
            if (entityTarget.getState() == EventState.PUBLISHED) {
                throw new ForbiddenException("Event already published.");
            }

            entityTarget.setState(EventState.CANCELED);
        } else if (stateAction == EventStateAction.CANCEL_REVIEW) {
            entityTarget.setState(EventState.CANCELED);
        } else if (stateAction == EventStateAction.SEND_TO_REVIEW) {
            entityTarget.setState(EventState.PENDING);
        }

    }

    private void checkUser(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("User by id=" + userId + " was not found.");
        }
    }

    private void checkEvent(Long eventId) {
        if (!eventRepo.existsById(eventId)) {
            throw new NotFoundException("Event by id=" + eventId + " was not found.");
        }
    }

    private void validateEventTime(LocalDateTime dateTime, int hours) {
        if (dateTime == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.plusHours(hours).isAfter(dateTime)) {
            throw new ForbiddenException("Must contain a date that has not yet arrived.");
        }
    }

    private void sendStat(HttpServletRequest request) {

        new Thread(() -> {
            HitDtoRequest dto = new HitDtoRequest();
            dto.setApp("ewm-main-service");
            dto.setIp(request.getRemoteAddr());
            dto.setTimestamp(LocalDateTime.now());
            dto.setUri(request.getRequestURI());
            try {
                ResponseEntity<Object> result = statsClient.createHit(dto);
                if (result.getStatusCode() == HttpStatus.CREATED) {
                    log.info("STAT: created hit={}, status={}", dto, result.getStatusCode());
                } else {
                    log.info("STAT: error created hit={}, status={}", dto, result.getStatusCode());
                }
            } catch (RuntimeException ex) {
                log.info("Create hit error: " + ex.getMessage());
            }
        }).start();
    }
}
