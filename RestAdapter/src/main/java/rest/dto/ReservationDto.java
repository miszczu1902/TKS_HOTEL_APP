package rest.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class ReservationDto {

    @NonNull
    @NotNull
    private Integer roomNumber;

    @NonNull
    @NotNull
    private String beginTime;

    @NonNull
    @NotNull
    private String endTime;

    @NonNull
    @NotNull
    private String username;
}
