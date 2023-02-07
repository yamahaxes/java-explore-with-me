package ru.practicum.ewm.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDtoNew {

    @NotBlank
    private String name;

    @Email
    private String email;
}
