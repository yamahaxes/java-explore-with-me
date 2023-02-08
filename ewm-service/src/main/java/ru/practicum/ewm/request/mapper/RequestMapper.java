package ru.practicum.ewm.request.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.user.model.User;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    private void setupModelMapper() {
        Converter<Event, Long> eventToLongConverter = mappingContext -> mappingContext.getSource().getId();
        Converter<User, Long> userToLongConverter = mappingContext -> mappingContext.getSource().getId();

        modelMapper.createTypeMap(Request.class, RequestDto.class)
                .addMappings(mapper -> mapper.using(eventToLongConverter).map(Request::getEvent, RequestDto::setEvent))
                .addMappings(mapper -> mapper.using(userToLongConverter).map(Request::getRequester, RequestDto::setRequester));

    }

    public RequestDto toDto(Request model) {
        return modelMapper.map(model, RequestDto.class);
    }

    public List<RequestDto> toDtoList(List<Request> models) {
        return models.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
