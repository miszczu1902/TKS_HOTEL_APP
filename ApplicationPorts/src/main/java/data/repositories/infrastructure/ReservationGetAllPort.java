package data.repositories.infrastructure;

import model.Reservation;

import java.util.List;

public interface ReservationGetAllPort {
    List<Reservation> getAll();
}
