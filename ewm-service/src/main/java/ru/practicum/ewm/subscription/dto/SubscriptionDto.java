package ru.practicum.ewm.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.user.dto.UserDtoShort;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDto {

    private Long id;

    private UserDtoShort subscriber;

    private UserDtoShort person;

}
