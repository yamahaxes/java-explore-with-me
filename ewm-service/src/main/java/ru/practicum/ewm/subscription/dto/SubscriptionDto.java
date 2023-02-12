package ru.practicum.ewm.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.user.dto.UserDtoShort;

@Getter
@Setter
@AllArgsConstructor
public class SubscriptionDto {

    private Long id;

    private UserDtoShort person;

}
