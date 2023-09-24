package ru.practicum.ewm.service.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.service.model.enums.State;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "EVENTS")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;
    @Column(name = "event_title")
    private String title;
    @Column(name = "annotation")
    private String annotation;
    @Column(name = "description")
    private String description;
    @CreationTimestamp
    @Column(name = "create_date")
    private LocalDateTime createDate;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @Column(name = "paid")
    private Boolean isPaid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "published_date")
    private LocalDateTime publishedDate;
    @Column(name = "request_moderation")
    private Boolean isRequestModeration;
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @Transient
    private Long views;
    @Transient
    private  Long confirmedRequests;
}
