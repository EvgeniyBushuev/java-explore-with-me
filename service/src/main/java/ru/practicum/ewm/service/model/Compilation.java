package ru.practicum.ewm.service.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.service.dto.event.ShortEventDto;

import javax.persistence.*;
import java.util.List;

@Table(name = "COMPILATIONS")
@Entity
@Setter
@Getter
@Builder
public class Compilation {
    @Id
    @Column(name = "compilation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "compilation_title")
    private String title;
    @Column(name = "compilation_pinned")
    private Boolean pinned;
    @ManyToMany
    @JoinTable(
            name = "EVENT_COMPILATION",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events;
}
