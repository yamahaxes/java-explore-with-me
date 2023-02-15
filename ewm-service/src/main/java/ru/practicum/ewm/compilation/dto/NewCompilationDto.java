package ru.practicum.ewm.compilation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Setter
@Getter
public class NewCompilationDto {

    private Set<Long> events = new HashSet<>();

    private Boolean pinned = false;

    @NotBlank
    private String title;

}
