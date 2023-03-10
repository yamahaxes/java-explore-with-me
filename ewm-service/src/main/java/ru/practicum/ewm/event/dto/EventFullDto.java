package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserDtoShort;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class EventFullDto {
    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests = 0L;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Long id;

    private UserDtoShort initiator;

    private Location location;

    private Boolean paid = true;

    private Integer participantLimit = 0;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private Boolean requestModeration = true;

    private EventState state;

    private String title;

    private Long views = 0L;

}
