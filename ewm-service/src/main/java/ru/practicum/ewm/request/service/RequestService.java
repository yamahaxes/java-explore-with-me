package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.dto.EventState;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repo.EventRepo;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestStatus;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repo.RequestRepo;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repo.UserRepo;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepo requestRepo;
    private final RequestMapper requestMapper;
    private final UserRepo userRepo;
    private final EventRepo eventRepo;

    public List<RequestDto> getRequests(Long userId) {
        return requestMapper.toDtoList(requestRepo.getRequestsByRequesterId(userId));
    }

    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {

        checkUser(userId);
        checkEvent(eventId);

        User requester = userRepo.getReferenceById(userId);
        Event event = eventRepo.getReferenceById(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("User with id=" + userId + " is the owner of the event by id=" + eventId);
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Event by id=" + eventId + " was not published");
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ForbiddenException("Limit exhausted.");
        }

        Request newRequest = new Request();
        newRequest.setCreated(LocalDateTime.now());
        newRequest.setRequester(requester);
        newRequest.setEvent(event);

        if (event.getRequestModeration()) {
            newRequest.setStatus(RequestStatus.PENDING);
        } else {
            newRequest.setStatus(RequestStatus.CONFIRMED);
            event.incConfirmedRequests();
            eventRepo.save(event);
        }

        return requestMapper.toDto(requestRepo.save(newRequest));
    }

    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        checkUser(userId);

        Request request = requestRepo.getRequestByIdIsAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request by id=" + requestId + " and user id=" + userId + " was not found."));

        Event event = request.getEvent();
        event.setConfirmedRequests(event.getConfirmedRequests() - 1);

        if (request.getStatus() == RequestStatus.CONFIRMED) {
            event.decConfirmedRequests();
            eventRepo.save(event);
        }

        request.setStatus(RequestStatus.CANCELED);
        requestRepo.save(request);

        return requestMapper.toDto(request);
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
}
