package data.repositories.infrastructure;

import model.Reservation;

import java.util.List;

public interface ReservationFindPort {
    List<Reservation> find(Object... elements);
}
