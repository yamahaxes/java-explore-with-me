package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.dto.EventDtoUpdateAdmin;
import ru.practicum.ewm.event.dto.EventFullDto;
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

    public EventFullDto updateEvent(Long eventId, EventDtoUpdateAdmin dto) {
        checkEvent(eventId);

        Event entityTarget = eventRepo.getReferenceById(eventId);
        Event src = eventMapper.toModel(dto);

        EwmUtils.copyNotNullProperties(src, entityTarget);

        return eventMapper.toEventFullDto(
                eventRepo.save(entityTarget)
        );
    }

    private void checkEvent(Long eventId) {
        if(!eventRepo.existsById(eventId)) {
            throw new NotFoundException("Event by id=" + eventId + " was not found.");
        }
    }
}
