package services;

import data.control.ReservationControlPort;
import data.infrastructure.ReservationInfPort;
import data.infrastructure.RoomInfPort;
import data.infrastructure.UserInfPort;
import domain.exceptions.LogicException;
import domain.exceptions.ReservationException;
import domain.exceptions.RoomException;
import domain.exceptions.UserException;
import jakarta.transaction.Transactional;
import domain.model.Reservation;
import domain.model.room.Room;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Logger;

public class ReservationService {

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
        return !(reservationInfPort.getAll().stream()
                .filter(reservation -> reservation.getRoom().getRoomNumber().equals(roomNumber) &&
                        (beginTime.isBefore(reservation.getEndTime()) ||
                                beginTime.equals(reservation.getEndTime())))
                .toList()).isEmpty();
    }

    @Transactional(dontRollbackOn = NoSuchElementException.class)
//    @Lock(value = LockType.READ)
    public void reserveRoom(Reservation reservation) throws LogicException {
        try {
//            roomControlPort.getRepository().getEntityTransaction().begin();
            int roomNumber = reservation.getRoom().getRoomNumber();
            Room room = roomInfPort.get(roomNumber);
//            roomControlPort.getRepository().getEntityManager().lock(room, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
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
//                    roomControlPort.getRepository().getEntityTransaction().commit();
                }
            } else {
                throw new UserException("Client is not active");
            }
        } catch (Exception e) {
//            roomControlPort.getRepository().getEntityTransaction().rollback();
            log.warning("Reservation couldn't be added");
            throw e;
        }
    }

    @Transactional(dontRollbackOn = NoSuchElementException.class)
//    @Lock(value = LockType.READ)
    public void endRoomReservation(UUID reservationId) throws LogicException {
        //            reservationInfPort.getRepository().getEntityTransaction().begin();
        LocalDate now = LocalDate.now();
        Reservation reservation = reservationInfPort.get(reservationId);

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
//            reservationInfPort.getRepository().getEntityTransaction().commit();
    }

    public Reservation aboutReservation(UUID reservationId) throws ReservationException {
        try {
            return reservationInfPort.get(reservationId);
        } catch (NoSuchElementException e) {
            log.warning("Any reservation for a given id %s doesn't exist".formatted(reservationId));
            throw new ReservationException("Any reservation for a given condition doesn't exist");
        }
    }

    public List<Reservation> getAllReservations() {
        return reservationInfPort.getAll();
    }

    public List<Reservation> getReservationsForClient(String username) throws ReservationException {
        try {
            return reservationInfPort.find(username);
        } catch (NoSuchElementException e) {
            log.warning("Any reservation for a given username %s doesn't exist".formatted(username));
            throw new ReservationException("Any reservation for a given condition doesn't exist");
        }
    }

    public List<Reservation> getReservationsForRoom(int roomNumber) throws ReservationException {
        try {
            return reservationInfPort.find(roomNumber);
        } catch (NoSuchElementException e) {
            log.warning("Any reservation for a given room with number %s doesn't exist".formatted(roomNumber));
            throw new ReservationException("Any reservation for a given condition doesn't exist");
        }
    }
}
