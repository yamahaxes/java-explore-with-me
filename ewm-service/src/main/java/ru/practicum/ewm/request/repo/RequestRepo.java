package ru.practicum.ewm.request.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.dto.RequestStatus;
import ru.practicum.ewm.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepo extends JpaRepository<Request, Long> {

    List<Request> getRequestsByRequesterId(Long requesterId);

    Optional<Request> getRequestByIdIsAndRequesterId(Long id, Long requesterId);

    List<Request> getRequestsByEventIdAndEventInitiatorId(Long eventId, Long userId);

    List<Request> getRequestsByEventIdAndEventInitiatorIdAndStatus(Long eventId, Long initiatorId, RequestStatus status);

}
