package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryDtoNew;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repo.CategoryRepo;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.util.Page;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;

    public CategoryDto createCategory(CategoryDtoNew dto) {
        Category entity = modelMapper.map(dto, Category.class);
        return modelMapper.map(categoryRepo.save(entity), CategoryDto.class);
    }

    public void deleteCategory(Long catId) {
        checkCategory(catId);
        categoryRepo.deleteById(catId);
    }

    public CategoryDto updateCategory(Long catId, CategoryDtoNew dto) {
        checkCategory(catId);
        Category entity = categoryRepo.getReferenceById(catId);

        if (!entity.getName().equals(dto.getName())) {
            entity.setName(dto.getName());
            return modelMapper.map(categoryRepo.save(entity), CategoryDto.class);
        }
        return modelMapper.map(entity, CategoryDto.class);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        return categoryRepo.findAll(new Page(from, size))
                .toList().stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategory(Long catId) {
        checkCategory(catId);
        return modelMapper.map(categoryRepo.getReferenceById(catId), CategoryDto.class);
    }

    private void checkCategory(Long catId) {
        if (!categoryRepo.existsById(catId)) {
            throw new NotFoundException("Category by id=" + catId + " was not found.");
        }
    }
}
