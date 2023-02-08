package ru.practicum.ewm.request.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.model.Request;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestMapper {

    private final ModelMapper modelMapper;

    public RequestDto toDto(Request model) {
        return modelMapper.map(model, RequestDto.class);
    }

    public List<RequestDto> toDtoList(List<Request> models) {
        return models.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
