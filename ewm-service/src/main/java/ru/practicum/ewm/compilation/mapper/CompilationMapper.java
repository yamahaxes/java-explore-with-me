package ru.practicum.ewm.compilation.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.repo.EventRepo;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationMapper {

    private final ModelMapper modelMapper;
    private final EventRepo eventRepo;

    @PostConstruct
    private void setupModelMapper() {
        modelMapper.createTypeMap(NewCompilationDto.class, Compilation.class)
                .addMappings(mapper -> mapper.skip(Compilation::setEvents));
    }

    public Compilation toModel(NewCompilationDto dto) {
        Compilation compilation = modelMapper.map(dto, Compilation.class);

        compilation.setEvents(eventRepo.getEventsByIdIn(dto.getEvents()));

        return compilation;
    }

    public Compilation toModel(UpdateCompilationDto dto) {
        Compilation compilation = modelMapper.map(dto, Compilation.class);

        compilation.setEvents(eventRepo.getEventsByIdIn(dto.getEvents()));

        return compilation;
    }

    public CompilationDto toDto(Compilation model) {
        return modelMapper.map(model, CompilationDto.class);
    }

    public List<CompilationDto> toDtoList(List<Compilation> models) {
        return models.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
