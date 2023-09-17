package ru.practicum.ewm.service.dto.request;

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
