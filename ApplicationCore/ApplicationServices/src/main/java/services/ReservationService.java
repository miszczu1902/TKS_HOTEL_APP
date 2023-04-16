package services;

import data.port.control.ReservationControlPort;
import data.port.infrastructure.ReservationInfPort;
import data.port.infrastructure.RoomInfPort;
import data.port.infrastructure.UserInfPort;
import domain.exceptions.LogicException;
import domain.exceptions.ReservationException;
import domain.exceptions.RoomException;
import domain.exceptions.UserException;
import domain.model.Reservation;
import domain.model.room.Room;
import service.port.control.ReservationControlServicePort;
import service.port.infrasturcture.ReservationInfServicePort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

@ApplicationScoped
@Transactional(dontRollbackOn = NoSuchElementException.class)
public class ReservationService implements ReservationInfServicePort, ReservationControlServicePort {

    @Inject
    private UserInfPort userInfPort;

    @Inject
    private RoomInfPort roomInfPort;

    @Inject
    private ReservationInfPort reservationInfPort;

    @Inject
    private ReservationControlPort reservationControlPort;

    private final Logger log = Logger.getLogger(getClass().getName());

    private boolean checkIfRoomCantBeReserved(int roomNumber, LocalDate beginTime) {
        List<Reservation> reservations = Optional.of(reservationInfPort.getAll()).orElse(Collections.emptyList());
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
            Room room = roomInfPort.get(roomNumber);
            String username = reservation.getUser().getUsername();

            if (userInfPort.get(username).getIsActive()) {
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
                    Reservation newReservation = new Reservation(room, beginTime, endTime, userInfPort.get(username));
                    newReservation.calculateReservationCost();
                    reservationControlPort.add(newReservation);
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
        Reservation reservation = reservationInfPort.get(UUID.fromString(reservationId));

        if (reservation.getBeginTime().isAfter(now)) {
            reservationControlPort.remove(reservation);
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
            reservationControlPort.update(reservation);
        }
    }

    @Override
    public Reservation getReservationById(String reservationId) throws ReservationException {
        try {
            return reservationInfPort.get(UUID.fromString(reservationId));
        } catch (NoSuchElementException e) {
            log.warning("Any reservation for a given id %s doesn't exist".formatted(reservationId));
            throw new ReservationException("Any reservation for a given condition doesn't exist");
        }
    }

    @Override
    public List<Reservation> getAllReservations() {
        return Optional.ofNullable(reservationInfPort.getAll()).orElse(Collections.emptyList());
    }

    @Override
    public List<Reservation> getReservationsForClient(String username) throws ReservationException {
        try {
            return reservationInfPort.find(username);
        } catch (NoSuchElementException e) {
            log.warning("Any reservation for a given username %s doesn't exist".formatted(username));
            throw new ReservationException("Any reservation for a given condition doesn't exist");
        }
    }

    @Override
    public List<Reservation> getReservationsForRoom(int roomNumber) throws ReservationException {
        try {
            return reservationInfPort.getReservationsByRoomNumber(roomNumber);
        } catch (NoSuchElementException e) {
            log.warning("Any reservation for a given room with number %s doesn't exist".formatted(roomNumber));
            throw new ReservationException("Any reservation for a given condition doesn't exist");
        }
    }
}
