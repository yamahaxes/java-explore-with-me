package ru.practicum.ewm.compilation.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.compilation.model.Compilation;

public interface CompilationRepo extends JpaRepository<Compilation, Long>,
        QuerydslPredicateExecutor<Compilation> {
}
