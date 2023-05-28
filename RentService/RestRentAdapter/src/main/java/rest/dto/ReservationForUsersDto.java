package rest.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ReservationForUsersDto {
    @NonNull
    @NotNull
    private Integer roomNumber;

    @NonNull
    @NotNull
    private LocalDate beginTime;

    @NonNull
    @NotNull
    private LocalDate endTime;
}
