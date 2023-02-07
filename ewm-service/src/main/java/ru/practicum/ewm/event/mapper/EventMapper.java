package ru.practicum.ewm.event.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repo.CategoryRepo;
import ru.practicum.ewm.event.dto.EventDtoUpdateAdmin;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.NotFoundException;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventMapper {

    private final ModelMapper modelMapper;
    private final CategoryRepo categoryRepo;

    @PostConstruct
    private void setupModelMapper() {
        Converter<Long, Category> longToCategoryConverter = mappingContext -> {
            if (!categoryRepo.existsById(mappingContext.getSource())) {
                throw new NotFoundException("Category by id=" + mappingContext.getSource() +
                        " was not found.");
            }
            return categoryRepo.getReferenceById(mappingContext.getSource());
        };

        modelMapper.createTypeMap(EventDtoUpdateAdmin.class, Event.class)
                .addMappings(mapper -> mapper.using(longToCategoryConverter).map(EventDtoUpdateAdmin::getCategory, Event::setCategory));

    }

    public EventFullDto toEventFullDto(Event model) {
        return modelMapper.map(model, EventFullDto.class);
    }

    public List<EventFullDto> toEventFullDtoList(List<Event> models) {
        return models.stream()
                .map(this::toEventFullDto)
                .collect(Collectors.toList());
    }

    public <T> Event toModel(T dto) {
        return modelMapper.map(dto, Event.class);
    }
}
