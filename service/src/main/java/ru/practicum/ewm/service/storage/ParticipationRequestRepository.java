package ru.practicum.ewm.service.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.service.dto.request.ParticipationRequestStats;
import ru.practicum.ewm.service.model.ParticipationRequest;
import ru.practicum.ewm.service.model.enus.Status;

import java.util.List;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByEventId(Long id);
    List<ParticipationRequest> findAllByIdIn(List<Long> ids);
    List<ParticipationRequest> findAllByRequesterId(Long id);
    @Query("select count(pr) from ParticipationRequests pr where pr.event.id = :eventId and pr.status = :status")
    Integer countByEventIdAndStatus(@Param("eventId") Long eventId,
                                 @Param("status") Status status);

    @Query("select new ru.practicum.service.dto.request.ParticipationRequestStats(r.event.id, count(r.id)) " +
            "from Request as r " +
            "where r.event.id in ?1 " +
            "and r.status = 'CONFIRMED' " +
            "group by r.event.id")
    List<ParticipationRequestStats> getConfirmedRequests(List<Long> eventsId);
}
