package ru.practicum.ewm.stats.server.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select h.app as app, h.uri as uri, count(distinct h.ip) as hits " +
            "from Hit h " +
            "where h.requestTime between :start and :end " +
            "and ((:uris) is null or h.uri in :uris) " +
            "group by h.app, h.uri " +
            "order by hits desc")
    List<ViewStatsDto> findUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select h.app as app, h.uri as uri, count(h.ip) as hits " +
            "from Hit h " +
            "where h.requestTime between :start and :end " +
            "and ((:uris) is null or h.uri in :uris) " +
            "group by h.app, h.uri " +
            "order by hits desc")
    List<ViewStatsDto> findNotUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uris);

}
