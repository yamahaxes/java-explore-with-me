package ru.practicum.ewm.category.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.category.model.Category;

public interface CategoryRepo extends JpaRepository<Category, Long> {

}
