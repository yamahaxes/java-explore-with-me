package ru.practicum.ewm.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
public class UserDtoNew {

    @NotBlank
    private String name;

    @Email
    private String email;
}
