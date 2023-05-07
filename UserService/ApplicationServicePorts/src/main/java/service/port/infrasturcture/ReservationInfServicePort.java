package service.port.infrasturcture;

import domain.exceptions.ReservationException;
import domain.model.Reservation;

import java.util.List;

public interface ReservationInfServicePort {

    List<Reservation> getAllReservations();

    Reservation getReservationById(String reservationId) throws ReservationException;

    List<Reservation> getReservationsForClient(String username) throws ReservationException;

    List<Reservation> getReservationsForRoom(int roomNumber) throws ReservationException;
}
