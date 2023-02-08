package ru.practicum.ewm.request.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepo extends JpaRepository<Request, Long> {

    List<Request> getRequestsByRequesterId(Long requester_id);

    Optional<Request> getRequestByIdIsAndRequesterId(Long id, Long requester_id);

}
