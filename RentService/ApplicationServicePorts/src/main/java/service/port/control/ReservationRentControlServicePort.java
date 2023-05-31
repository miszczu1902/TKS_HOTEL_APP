package service.port.control;

import domain.exceptions.LogicException;
import domain.model.Reservation;

public interface ReservationRentControlServicePort {

    void reserveRoom(Reservation reservation) throws LogicException;

    void endReserveRoom(String reservationId) throws LogicException;

}
