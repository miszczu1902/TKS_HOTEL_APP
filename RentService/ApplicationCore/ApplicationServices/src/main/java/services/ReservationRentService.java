package services;

import data.port.control.ReservationRentControlPort;
import data.port.infrastructure.ReservationRentInfPort;
import data.port.infrastructure.RoomRentInfPort;
import data.port.infrastructure.UserRentInfPort;
import domain.exceptions.LogicException;
import domain.exceptions.ReservationException;
import domain.exceptions.RoomException;
import domain.exceptions.UserException;
import domain.model.Reservation;
import domain.model.room.Room;
import domain.model.user.User;
import service.port.control.ReservationRentControlServicePort;
import service.port.infrasturcture.ReservationRentInfServicePort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

@ApplicationScoped
@Transactional(dontRollbackOn = NoSuchElementException.class)
public class ReservationRentService implements ReservationRentInfServicePort, ReservationRentControlServicePort {

    @Inject
    private UserRentInfPort userRentInfPort;

    @Inject
    private RoomRentInfPort roomRentInfPort;

    @Inject
    private ReservationRentInfPort reservationRentInfPort;

    @Inject
    private ReservationRentControlPort reservationRentControlPort;

    private final Logger log = Logger.getLogger(getClass().getName());

    private boolean checkIfRoomCantBeReserved(int roomNumber, LocalDate beginTime) {
        List<Reservation> reservations = Optional.of(reservationRentInfPort.getAll()).orElse(Collections.emptyList());
        if (reservations.isEmpty()) {
            return false;
        } else {
            return !(reservations.stream().filter(reservation ->
                            reservation.getRoom().getRoomNumber().equals(roomNumber)
                                    && (beginTime.isBefore(reservation.getEndTime()) || beginTime.equals(reservation.getEndTime())))
                    .toList()).isEmpty();
        }
    }

    @Override
    @Transactional(dontRollbackOn = NoSuchElementException.class)
    public void reserveRoom(Reservation reservation) throws LogicException {
        try {
            int roomNumber = reservation.getRoom().getRoomNumber();
            Room room = roomRentInfPort.get(roomNumber);
            String username = reservation.getUser().getUsername();

            User user = userRentInfPort.get(username);

            if (user != null) {
                if (user.getIsActive()) {
                    LocalDate beginTime = LocalDate.parse(reservation.getBeginTime().toString());
                    LocalDate endTime = LocalDate.parse(reservation.getEndTime().toString());
                    LocalDate now = LocalDate.now();

                    if (beginTime.isAfter(endTime) || endTime.isBefore(beginTime)) {
                        log.warning("Start time of reservation %s should be before end time reservation %s".formatted(beginTime, endTime));
                        throw new ReservationException("Start time of reservation should be before end time reservation");
                    } else if (beginTime.isBefore(now) || endTime.isBefore(now)) {
                        log.warning("Reservation with start time %s cannot be before current date %s".formatted(now, beginTime));
                        throw new ReservationException("Reservation cannot be before current date");
                    } else if (checkIfRoomCantBeReserved(roomNumber, beginTime)) {
                        log.warning("Room %s is currently reserved".formatted(roomNumber));
                        throw new RoomException("Room is currently reserved");
                    } else {
                        Reservation newReservation = new Reservation(room, beginTime, endTime, userRentInfPort.get(username));
                        newReservation.calculateReservationCost();
                        reservationRentControlPort.add(newReservation);
                    }
                } else {
                    throw new ReservationException("Client is not active");
                }
            } else {
                throw new UserException("Client is not active");
            }
        } catch (Exception e) {
            log.warning("Reservation couldn't be added");
            throw e;
        }
    }

    @Override
    public void endReserveRoom(String reservationId) throws LogicException {
        LocalDate now = LocalDate.now();
        Reservation reservation = reservationRentInfPort.get(UUID.fromString(reservationId));

        if (reservation.getBeginTime().isAfter(now)) {
            reservationRentControlPort.remove(reservation);
        } else if (reservation.getBeginTime().isBefore(now)
                && reservation.getEndTime().isBefore(now)) {
            reservation.calculateReservationCost();
        } else {
            if (now.isBefore(reservation.getEndTime())) {
                reservation.setEndTime(now);
            } else {
                log.warning("Reservation couldn't be ended");
                throw new ReservationException("Reservation couldn't be ended");
            }
            reservation.calculateReservationCost();

            reservation.setActive(false);
            reservationRentControlPort.update(reservation);
        }
    }

    @Override
    public Reservation getReservationById(String reservationId) throws ReservationException {
        try {
            return reservationRentInfPort.get(UUID.fromString(reservationId));
        } catch (NoSuchElementException e) {
            log.warning("Any reservation for a given id %s doesn't exist".formatted(reservationId));
            throw new ReservationException("Any reservation for a given condition doesn't exist");
        }
    }

    @Override
    public List<Reservation> getAllReservations() {
        return Optional.ofNullable(reservationRentInfPort.getAll()).orElse(Collections.emptyList());
    }

    @Override
    public List<Reservation> getReservationsForClient(String username) throws ReservationException {
        try {
            return reservationRentInfPort.find(username);
        } catch (NoSuchElementException e) {
            log.warning("Any reservation for a given username %s doesn't exist".formatted(username));
            throw new ReservationException("Any reservation for a given condition doesn't exist");
        }
    }

    @Override
    public List<Reservation> getReservationsForRoom(int roomNumber) throws ReservationException {
        try {
            return reservationRentInfPort.getReservationsByRoomNumber(roomNumber);
        } catch (NoSuchElementException e) {
            log.warning("Any reservation for a given room with number %s doesn't exist".formatted(roomNumber));
            throw new ReservationException("Any reservation for a given condition doesn't exist");
        }
    }
}
