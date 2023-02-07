package ru.practicum.ewm.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryDtoNew {

    @NotBlank
    private String name;

}
