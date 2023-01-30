package ru.practicum.ewm.stats.server.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.dto.HitDtoResponse;
import ru.practicum.ewm.stats.server.model.Hit;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatsServerRepo extends JpaRepository<Hit, Long> {

    @Query( "SELECT " +
            "   new ru.practicum.ewm.stats.dto.HitDtoResponse(h.app, h.uri, COUNT(h.id)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.id) DESC ")
    List<HitDtoResponse> getStats(LocalDateTime start, LocalDateTime end);

    @Query( "SELECT " +
            "   new ru.practicum.ewm.stats.dto.HitDtoResponse(h.app, h.uri, COUNT(h.id)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 AND h.uri IN (?3) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.id) DESC ")
    List<HitDtoResponse> getStats(LocalDateTime start, LocalDateTime end, Collection<String> uris);

    @Query( "SELECT " +
            "   new ru.practicum.ewm.stats.dto.HitDtoResponse(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC ")
    List<HitDtoResponse> getStatsUnique(LocalDateTime start, LocalDateTime end);

    @Query( "SELECT " +
            "   new ru.practicum.ewm.stats.dto.HitDtoResponse(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 AND h.uri IN (?3) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC ")
    List<HitDtoResponse> getStatsUnique(LocalDateTime start, LocalDateTime end, Collection<String> uris);

}
