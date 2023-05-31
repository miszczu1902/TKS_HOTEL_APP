package adapter;

import domain.exceptions.LogicException;
import domain.exceptions.ReservationException;
import domain.model.Reservation;
import service.port.control.ReservationRentControlServicePort;
import service.port.infrasturcture.ReservationRentInfServicePort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class RestReservationAdapter {

    @Inject
    private ReservationRentInfServicePort reservationRentInfServicePort;

    @Inject
    private ReservationRentControlServicePort reservationRentControlServicePort;
    
    public void reserveRoom(Reservation reservation) throws LogicException {
        reservationRentControlServicePort.reserveRoom(reservation);
    }

    public void endReserveRoom(String reservationId) throws LogicException {
        reservationRentControlServicePort.endReserveRoom(reservationId);
    }

    public List<Reservation> getAllReservations() {
        return reservationRentInfServicePort.getAllReservations();
    }

    public Reservation getReservationById(String reservationId) throws ReservationException {
        return reservationRentInfServicePort.getReservationById(reservationId);
    }

    public List<Reservation> getReservationsForClient(String username) throws ReservationException {
        return reservationRentInfServicePort.getReservationsForClient(username);
    }

    public List<Reservation> getReservationsForRoom(int roomNumber) throws ReservationException {
        return reservationRentInfServicePort.getReservationsForRoom(roomNumber);
    }

}
