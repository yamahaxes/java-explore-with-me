package ru.practicum.ewm.compilation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class UpdateCompilationDto {

    private Set<Long> events;

    private Boolean pinned;

    private String title;
}
