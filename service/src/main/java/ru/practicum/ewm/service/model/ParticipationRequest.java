package ru.practicum.ewm.service.model;

import lombok.*;
import ru.practicum.ewm.service.model.enus.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "PARTICIPATION_REQUESTS")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_request_id")
    private Long id;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event; //TODO СОЗДАТЬ ИВЕНТ

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "participation_request_status")
    private Status status;
}
