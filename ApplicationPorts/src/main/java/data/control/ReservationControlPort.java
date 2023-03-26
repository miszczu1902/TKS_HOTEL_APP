package data.control;

import domain.model.Reservation;

public interface ReservationControlPort {

    void add(Reservation element);

    void remove(Reservation... elements);

    void update(Reservation... elements);


}
