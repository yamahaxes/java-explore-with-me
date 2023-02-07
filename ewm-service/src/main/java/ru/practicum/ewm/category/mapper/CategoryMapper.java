package ru.practicum.ewm.category.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.Category;

@Service
@RequiredArgsConstructor
public class CategoryMapper {
    private final ModelMapper mapper;

    public <T> Category toModel(T dto) {
        return mapper.map(dto, Category.class);
    }

    public CategoryDto toCategoryDto(Category model) {
        return mapper.map(model, CategoryDto.class);
    }

}
