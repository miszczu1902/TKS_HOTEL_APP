package data.repositories.infrastructure;

import model.Reservation;

public interface ReservationGetPort {
    Reservation get(Object element);
}
