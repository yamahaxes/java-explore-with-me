package ru.practicum.ewm.stats.server.repo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatData {

    private String app;

    private String uri;

    private long count;

    private long unique;

}
