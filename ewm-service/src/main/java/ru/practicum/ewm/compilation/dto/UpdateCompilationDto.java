package ru.practicum.ewm.compilation.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UpdateCompilationDto {

    private Set<Long> events;

    private Boolean pinned;

    private String title;
}
