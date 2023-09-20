package ru.practicum.ewm.service.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.service.model.Event;
import ru.practicum.ewm.service.model.enus.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select e from Event e " +
            "where ((:users) is null or e.initiator.id in :users) " +
            "and ((:states) is null or e.state in :states) " +
            "and ((:categories) is null or e.category.id in :categories) " +
            "and (cast(:rangeStart as date) is null or e.eventDate >= :rangeStart)" +
            "and (cast(:rangeEnd as date) is null or e.eventDate <= :rangeEnd)")
    List<Event> findAllByAdmin(@Param("users") List<Long> users,
                               @Param("states") List<State> states,
                               @Param("categories") List<Long> categories,
                               @Param("rangeStart") LocalDateTime rangeStart,
                               @Param("rangeEnd") LocalDateTime rangeEnd,
                               Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    @Query("select e from Event e " +
            "where (e.state = 'PUBLISHED') " +
            "and (lower(e.annotation) like lower(concat('%', :text, '%')) or lower(e.description) like lower(concat('%', :text, '%'))) " +
            "and ((:categories) is null or e.category.id in :categories) " +
            "and ((:paid) is null or e.isPaid = :paid) " +
            "and (cast(:rangeStart as date) is null or e.eventDate >= :rangeStart)" +
            "and (cast(:rangeEnd as date) is null or e.eventDate <= :rangeEnd)")
    List<Event> getAllByPublic(@Param("text") String text,
                               @Param("categories") List<Long> categories,
                               @Param("paid") Boolean paid,
                               @Param("rangeStart") LocalDateTime rangeStart,
                               @Param("rangeEnd") LocalDateTime rangeEnd,
                               Pageable pageable);

    @Query("select e from Event e " +
            "where ((:text is null or upper(e.annotation) like upper(concat('%', :text, '%'))) " +
            "or (:text is null or upper(e.description) like upper(concat('%', :text, '%')))) " +
            "and (:state is null or e.state = :state) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (:paid is null or e.isPaid = :paid) " +
            "and (cast(:rangeStart as date) is null or e.eventDate >= :rangeStart) " +
            "and (cast(:rangeEnd as date) is null or e.eventDate <= :rangeEnd) ")
    List<Event> getAllEvents(@Param("text") String text,
                             @Param("state") State state,
                             @Param("categories") List<Long> categories,
                             @Param("paid") Boolean paid,
                             @Param("rangeStart") LocalDateTime rangeStart,
                             @Param("rangeEnd") LocalDateTime rangeEnd,
                             Pageable pageable);

    @Query("select e from Event e " +
            "where (:users is null or e.initiator.id in :users) " +
            "and (:states is null or e.state in :states) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (cast(:rangeStart as date) is null or e.eventDate >= :rangeStart) " +
            "and (cast(:rangeEnd as date) is null or e.eventDate <= :rangeEnd) ")
    List<Event> getEventsByUserId(@Param("users") List<Long> usersIds,
                                  @Param("states") List<State> states,
                                  @Param("categories") List<Long> categories,
                                  @Param("rangeStart") LocalDateTime rangeStart,
                                  @Param("rangeEnd") LocalDateTime rangeEnd,
                                  Pageable pageable);

    List<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    @Query("select count(e) from Event e " +
            "where (e.category.id = :categoryId)")
    Long countEventsWithCategory(@Param("categoryId") Long categoryId);

    @Query("select e from Event e " +
            "where ((:text is null or upper(e.annotation) like upper(concat('%', :text, '%'))) " +
            "or (:text is null or upper(e.description) like upper(concat('%', :text, '%')))) " +
            "and (:state is null or e.state = :state) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (:paid is null or e.isPaid = :paid) " +
            "and (cast(:rangeStart as date) is null or e.eventDate >= :rangeStart) " +
            "and (cast(:rangeEnd as date) is null or e.eventDate <= :rangeEnd) " +
            "AND (SELECT COUNT(pr) FROM ParticipationRequest pr WHERE pr.event.id = e.id " +
            "AND pr.status = 'APPROVE') < e.participantLimit")
    List<Event> getAvailableEvents(@Param("text") String text,
                                   @Param("state") State state,
                                   @Param("categories") List<Long> categories,
                                   @Param("paid") Boolean paid,
                                   @Param("rangeStart") LocalDateTime rangeStart,
                                   @Param("rangeEnd") LocalDateTime rangeEnd,
                                   Pageable pageable);

    List<Event> findAllByIdIn(List<Long> eventsId);
}