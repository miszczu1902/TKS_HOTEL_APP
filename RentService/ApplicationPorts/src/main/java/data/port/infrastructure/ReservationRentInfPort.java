package data.port.infrastructure;

import domain.model.Reservation;

import java.util.List;

public interface ReservationRentInfPort {

    List<Reservation> find(Object... elements);

    List<Reservation> getAll();

    Reservation get(Object element);

    List<Reservation> getReservationsByRoomNumber(Integer roomNumber);

}
