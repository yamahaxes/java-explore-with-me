package ru.practicum.ewm.compilation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class CompilationDto {

    private Long id;

    private Set<EventShortDto> events;

    private Boolean pinned;

    private String title;

}
