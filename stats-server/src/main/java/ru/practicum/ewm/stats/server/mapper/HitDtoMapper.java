package ru.practicum.ewm.stats.server.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.HitDtoRequest;
import ru.practicum.ewm.stats.server.model.Hit;


@Service
public class HitDtoMapper {

    public Hit fromDto(HitDtoRequest dto) {
        Hit hit = new Hit();
        hit.setApp(dto.getApp());
        hit.setUri(dto.getUri());
        hit.setIp(dto.getIp());
        hit.setTimestamp(dto.getTimestamp());

        return hit;
    }

    public HitDtoRequest toDto(Hit hit) {
        HitDtoRequest dto = new HitDtoRequest();
        dto.setApp(hit.getApp());
        dto.setUri(hit.getUri());
        dto.setIp(hit.getIp());
        dto.setTimestamp(hit.getTimestamp());

        return dto;
    }

}
