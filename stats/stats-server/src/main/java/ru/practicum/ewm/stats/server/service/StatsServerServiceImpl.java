package ru.practicum.ewm.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.HitDtoRequest;
import ru.practicum.ewm.stats.dto.HitDtoResponse;
import ru.practicum.ewm.stats.server.mapper.HitDtoMapper;
import ru.practicum.ewm.stats.server.model.Hit;
import ru.practicum.ewm.stats.server.repo.StatsServerRepo;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StatsServerServiceImpl implements StatsServerService {

    private final HitDtoMapper hitDtoMapper;
    private final StatsServerRepo repository;

    @Override
    public HitDtoRequest post(HitDtoRequest dtoRequest) {

        Hit model = hitDtoMapper.fromDto(dtoRequest);

        return hitDtoMapper.toDto(
                repository.save(model)
        );
    }

    @Override
    public List<HitDtoResponse> getStats(LocalDateTime start, LocalDateTime end, boolean unique) {

        if (unique) {
            return repository.getStatsUnique(start, end);
        }

        return repository.getStats(start, end);
    }

    @Override
    public List<HitDtoResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        if (unique) {
            return repository.getStatsUnique(start, end, uris);
        }

        return repository.getStats(start, end, uris);
    }
}
