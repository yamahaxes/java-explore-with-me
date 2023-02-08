package ru.practicum.ewm.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestDto {

    private LocalDateTime created;

    private Long event;

    private Long id;

    private Long requester;

    private RequestStatus status = RequestStatus.PENDING;
}
