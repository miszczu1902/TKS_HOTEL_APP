package rest.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class ReservationSelfDto {
    @NonNull
    @NotNull
    private Integer roomNumber;

    @NonNull
    @NotNull
    private String beginTime;

    @NonNull
    @NotNull
    private String endTime;
}
