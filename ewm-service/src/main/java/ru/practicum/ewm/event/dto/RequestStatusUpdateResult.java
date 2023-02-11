package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.request.dto.RequestDto;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class RequestStatusUpdateResult {

    private List<RequestDto> confirmedRequests = new ArrayList<>();

    private List<RequestDto> rejectedRequests = new ArrayList<>();

}
