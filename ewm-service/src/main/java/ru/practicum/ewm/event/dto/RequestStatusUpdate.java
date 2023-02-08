package ru.practicum.ewm.event.dto;

import lombok.Data;
import ru.practicum.ewm.request.dto.RequestStatus;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class RequestStatusUpdate {

    @NotNull
    private List<Long> requestIds = new ArrayList<>();

    @NotNull
    private RequestStatus status;

}
