package ru.practicum.ewm.compilation.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.model.QCompilation;
import ru.practicum.ewm.compilation.repo.CompilationRepo;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.util.EwmUtils;
import ru.practicum.ewm.util.Page;
import ru.practicum.ewm.util.QPredicates;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationService {

    private final CompilationMapper compilationMapper;
    private final CompilationRepo compilationRepo;

    public CompilationDto createCompilation(NewCompilationDto dto) {
        Compilation compilation = compilationMapper.toModel(dto);

        return compilationMapper.toDto(
            compilationRepo.save(compilation)
        );
    }

    public void deleteCompilation(Long compId) {
        checkCompilation(compId);
        compilationRepo.deleteById(compId);
    }

    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto dto) {
        checkCompilation(compId);

        Compilation compilationTarget = compilationRepo.getReferenceById(compId);
        Compilation src = compilationMapper.toModel(dto);

        EwmUtils.copyNotNullProperties(src, compilationTarget);

        return compilationMapper.toDto(
                compilationRepo.save(compilationTarget)
        );
    }

    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {

        QPredicates predicates = QPredicates.builder()
                .add(pinned, QCompilation.compilation.pinned::eq);

        Predicate predicate = predicates.buildAnd();
        Page page = new Page(from, size);

        List<Compilation> compilations = predicate == null ?
                compilationRepo.findAll(page).toList() :
                compilationRepo.findAll(predicate, page).toList();

        return compilationMapper.toDtoList(compilations);
    }

    public CompilationDto getCompilation(Long compId) {
        checkCompilation(compId);
        return compilationMapper.toDto(
                compilationRepo.getReferenceById(compId)
        );
    }

    private void checkCompilation(Long compId) {
        if (!compilationRepo.existsById(compId)) {
            throw new NotFoundException("Compilation by id=" + compId + " was not found.");
        }
    }
}
