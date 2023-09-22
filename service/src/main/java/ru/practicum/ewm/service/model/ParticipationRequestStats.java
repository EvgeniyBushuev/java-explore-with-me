package ru.practicum.ewm.service.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationRequestStats {
    Long eventId;
    Long confirmedRequests;
}