package ru.practicum.ewm.service.model;

import lombok.*;

import javax.persistence.*;

@Table(name = "LOCATIONS")
@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;
    @Column(name = "lat")
    private float lat;
    @Column(name = "lon")
    private float lon;
}