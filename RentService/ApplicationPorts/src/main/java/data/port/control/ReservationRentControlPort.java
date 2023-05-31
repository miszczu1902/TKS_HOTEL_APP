package data.port.control;

import domain.model.Reservation;

public interface ReservationRentControlPort {

    void add(Reservation element);

    void remove(Reservation... elements);

    void update(Reservation... elements);

}
