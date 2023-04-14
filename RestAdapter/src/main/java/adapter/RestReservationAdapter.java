package adapter;

import domain.exceptions.LogicException;
import domain.exceptions.ReservationException;
import domain.model.Reservation;
import domain.model.room.Room;
import domain.model.user.User;
import service.port.control.ReservationControlServicePort;
import service.port.infrasturcture.ReservationInfServicePort;
import services.ReservationService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class RestReservationAdapter implements ReservationInfServicePort, ReservationControlServicePort {

    @Inject
    private ReservationService reservationService;

    @Override
    public void reserveRoom(Reservation reservation) throws LogicException {
        reservationService.reserveRoom(reservation);
    }

    @Override
    public void endReserveRoom(String reservationId) throws LogicException {
        reservationService.endRoomReservation(UUID.fromString(reservationId));
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @Override
    public Reservation getReservationById(String reservationId) throws ReservationException {
        return reservationService.aboutReservation(UUID.fromString(reservationId));
    }

    @Override
    public List<Reservation> getReservationsForClient(String username) throws ReservationException {
        return reservationService.getReservationsForClient(username);
    }
    @Override
    public List<Reservation> getReservationsForRoom(int roomNumber) throws ReservationException {
        return reservationService.getReservationsForRoom(roomNumber);
    }

}
