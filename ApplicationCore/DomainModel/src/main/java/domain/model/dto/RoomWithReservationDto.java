package domain.model.dto;

import domain.model.room.EquipmentType;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RoomWithReservationDto {

    @NonNull
    @NotNull
    @Min(1)
    private Integer roomNumber;

    @NonNull
    @NotNull
    @Min(1)
    private Integer capacity;

    @NonNull
    @NotNull
    @Positive
    private Double price;

    @NonNull
    private EquipmentType equipmentType;

    @NonNull
    @NotNull
    List<ReservationForRoomsDto> reservations;
}
