package ru.practicum.ewm.compilation.controller.adm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
public class CompilationControllerAdm {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto dto) {
        log.info("POST createCompilation(): dto={}", dto);
        return compilationService.createCompilation(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCompilation(@Positive @PathVariable Long compId) {
        log.info("DELETE removeCompilation(): compId={}", compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@Positive @PathVariable Long compId,
                                            @RequestBody UpdateCompilationDto dto) {
        log.info("PATCH updateCompilation(): compId={}, dto={}", compId, dto);
        return compilationService.updateCompilation(compId, dto);
    }
}
