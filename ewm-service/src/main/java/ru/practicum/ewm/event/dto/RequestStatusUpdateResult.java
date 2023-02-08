package ru.practicum.ewm.event.dto;

import lombok.Data;
import ru.practicum.ewm.request.dto.RequestDto;

import java.util.ArrayList;
import java.util.List;

@Data
public class RequestStatusUpdateResult {

    private List<RequestDto> confirmedRequests = new ArrayList<>();

    private List<RequestDto> rejectedRequests = new ArrayList<>();

}
