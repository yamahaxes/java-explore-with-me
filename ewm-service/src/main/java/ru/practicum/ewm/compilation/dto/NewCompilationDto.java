package ru.practicum.ewm.compilation.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Data
public class NewCompilationDto {

    private Set<Long> events = new HashSet<>();

    private Boolean pinned = false;

    @NotBlank
    private String title;

}
