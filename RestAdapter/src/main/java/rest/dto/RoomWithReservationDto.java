package rest.dto;

import domain.model.room.EquipmentType;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Comparator;
import java.util.List;

@Data
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
public class RoomWithReservationDto implements Comparator<ReservationForRoomsDto> {

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

    @Override
    public int compare(ReservationForRoomsDto o1, ReservationForRoomsDto o2) {
        int result = o1.getBeginTime().compareTo(o2.getEndTime());
        if (result == 0) {
            result = o1.getEndTime().compareTo(o2.getBeginTime());
        }
        return result;
    }

    @Override
    public Comparator<ReservationForRoomsDto> reversed() {
        return Comparator.super.reversed();
    }
}
