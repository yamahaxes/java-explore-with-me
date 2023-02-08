package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserDtoShort;

import java.time.LocalDateTime;

@Data
public class EventShortDto {

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests = 0L;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Long id;

    private UserDtoShort initiator;

    private Boolean paid = true;

    private String title;

    private Long views;
}
