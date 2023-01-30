package ru.practicum.ewm.stats.server.service;

import ru.practicum.ewm.stats.dto.HitDtoRequest;
import ru.practicum.ewm.stats.dto.HitDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsServerService {

    HitDtoRequest post(HitDtoRequest dtoRequest);

    List<HitDtoResponse> getStats(LocalDateTime start,
                                  LocalDateTime end,
                                  boolean unique);

    List<HitDtoResponse> getStats(LocalDateTime start,
                                  LocalDateTime end,
                                  List<String> uris,
                                  boolean unique);
}
