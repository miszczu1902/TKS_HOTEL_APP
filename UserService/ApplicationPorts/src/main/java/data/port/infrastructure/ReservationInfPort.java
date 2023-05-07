package data.port.infrastructure;

import domain.model.Reservation;

import java.util.List;

public interface ReservationInfPort {

    List<Reservation> find(Object... elements);

    List<Reservation> getAll();

    Reservation get(Object element);

    List<Reservation> getReservationsByRoomNumber(Integer roomNumber);

}
