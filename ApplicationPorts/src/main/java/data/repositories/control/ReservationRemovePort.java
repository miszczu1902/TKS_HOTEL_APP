package data.repositories.control;

import model.Reservation;

public interface ReservationRemovePort {
    void remove(Reservation... elements);
}
