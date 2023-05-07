package adapter;

import domain.exceptions.LogicException;
import domain.exceptions.ReservationException;
import domain.model.Reservation;
import service.port.control.ReservationControlServicePort;
import service.port.infrasturcture.ReservationInfServicePort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class RestReservationAdapter {

    @Inject
    private ReservationInfServicePort reservationInfServicePort;

    @Inject
    private ReservationControlServicePort reservationControlServicePort;
    
    public void reserveRoom(Reservation reservation) throws LogicException {
        reservationControlServicePort.reserveRoom(reservation);
    }

    public void endReserveRoom(String reservationId) throws LogicException {
        reservationControlServicePort.endReserveRoom(reservationId);
    }

    public List<Reservation> getAllReservations() {
        return reservationInfServicePort.getAllReservations();
    }

    public Reservation getReservationById(String reservationId) throws ReservationException {
        return reservationInfServicePort.getReservationById(reservationId);
    }

    public List<Reservation> getReservationsForClient(String username) throws ReservationException {
        return reservationInfServicePort.getReservationsForClient(username);
    }

    public List<Reservation> getReservationsForRoom(int roomNumber) throws ReservationException {
        return reservationInfServicePort.getReservationsForRoom(roomNumber);
    }

}
