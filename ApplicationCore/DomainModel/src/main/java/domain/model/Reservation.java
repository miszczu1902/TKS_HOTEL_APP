package domain.model;


import domain.model.room.Room;
import domain.model.user.User;
import lombok.*;
import org.apache.commons.math3.util.Precision;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class Reservation implements Serializable {

    private UUID id;

    @NonNull
    private Room room;

    @NonNull
    private LocalDate beginTime;

    @NonNull
    private LocalDate endTime;

    @NonNull
    private User user;

    private double reservationCost;

    private boolean isActive = true;

    public int getRentDays() {
        return endTime.getDayOfYear() - beginTime.getDayOfYear();
    }

    public void calculateReservationCost() {
        reservationCost = Precision.round(getRentDays() * room.getPrice(), 2);
        if (reservationCost < 0) {
            reservationCost = 0;
        }
        if (getRentDays() == 0) {
            Precision.round((getRentDays() + 1) * room.getPrice(), 2);
        }
    }
}
