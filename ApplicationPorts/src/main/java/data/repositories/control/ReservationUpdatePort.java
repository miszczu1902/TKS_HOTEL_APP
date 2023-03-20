package data.repositories.control;

import model.Reservation;

public interface ReservationUpdatePort {
    void update(Reservation... elements);

}
