package rest.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
public class ReservationForRoomsDto {

    @NonNull
    @NotNull
    private LocalDate beginTime;

    @NonNull
    @NotNull
    private LocalDate endTime;

    @NonNull
    @NotNull
    private String username;
}
