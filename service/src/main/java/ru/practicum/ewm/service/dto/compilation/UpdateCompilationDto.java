package ru.practicum.ewm.service.dto.compilation;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationDto {
    @Size(max = 50)
    private String title;
    private Boolean pinned;
    private List<Long> events;
}
