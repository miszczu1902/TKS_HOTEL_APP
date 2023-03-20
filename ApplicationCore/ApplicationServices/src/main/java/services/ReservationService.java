package services;

import adapter.aggregates.ReservationRepositoryAdapter;
import adapter.aggregates.RoomRepositoryAdapter;
import adapter.aggregates.UserRepositoryAdapter;
import model.dto.ReservationDto;
import exceptions.UserException;
import exceptions.LogicException;
import exceptions.ReservationException;
import exceptions.RoomException;
import jakarta.persistence.*;
import model.Reservation;
import model.room.Room;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.List;

public class ReservationService {

    @Inject
    private UserRepositoryAdapter userRepository;

    @Inject
    private RoomRepositoryAdapter roomRepository;

    @Inject
    private ReservationRepositoryAdapter reservationRepository;

    private final Logger log = Logger.getLogger(getClass().getName());

    private boolean checkIfRoomCantBeReserved(int roomNumber, LocalDate beginTime) {
        return !(reservationRepository.getAll().stream()
                .filter(reservation -> reservation.getRoom().getRoomNumber().equals(roomNumber) &&
                        (beginTime.isBefore(reservation.getEndTime()) ||
                                beginTime.equals(reservation.getEndTime())))
                .toList()).isEmpty();
    }

    public void reserveRoom(ReservationDto reservation) throws LogicException {
        try {
            roomRepository.getRepository().getEntityTransaction().begin();
            int roomNumber = reservation.getRoomNumber();
            Room room = roomRepository.get(roomNumber);
            roomRepository.getRepository().getEntityManager().lock(room, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            String username = reservation.getUsername();

            if (userRepository.get(username).getIsActive()) {
                LocalDate beginTime = LocalDate.parse(reservation.getBeginTime());
                LocalDate endTime = LocalDate.parse(reservation.getEndTime());
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
                    Reservation newReservation = new Reservation(room, beginTime, endTime, userRepository.get(username));
                    newReservation.calculateReservationCost();
                    reservationRepository.add(newReservation);
                    roomRepository.getRepository().getEntityTransaction().commit();
                }
            } else {
                throw new UserException("Client is not active");
            }
        } catch (Exception e) {
            roomRepository.getRepository().getEntityTransaction().rollback();
            log.warning("Reservation couldn't be added");
            throw e;
        }
    }

    public void endRoomReservation(UUID reservationId) throws LogicException {
        try {
            reservationRepository.getRepository().getEntityTransaction().begin();
            LocalDate now = LocalDate.now();
            Reservation reservation = reservationRepository.get(reservationId);

            if (reservation.getBeginTime().isAfter(now)) {
                reservationRepository.remove(reservation);
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
                reservationRepository.update(reservation);
            }
            reservationRepository.getRepository().getEntityTransaction().commit();
        } catch (Exception e) {
            reservationRepository.getRepository().getEntityTransaction().rollback();
            throw e;
        }
    }

    public Reservation aboutReservation(UUID reservationId) throws ReservationException {
        try {
            return reservationRepository.get(reservationId);
        } catch (NoSuchElementException e) {
            log.warning("Any reservation for a given id %s doesn't exist".formatted(reservationId));
            throw new ReservationException("Any reservation for a given condition doesn't exist");
        }
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.getAll();
    }

    public List<Reservation> getReservationsForClient(String username) throws ReservationException {
        try {
            return reservationRepository.getReservationsForClient(username);
        } catch (NoSuchElementException e) {
            log.warning("Any reservation for a given username %s doesn't exist".formatted(username));
            throw new ReservationException("Any reservation for a given condition doesn't exist");
        }
    }

    public List<Reservation> getReservationsForRoom(int roomNumber) throws ReservationException {
        try {
            return reservationRepository.getReservationsForRoom(roomNumber);
        } catch (NoSuchElementException e) {
            log.warning("Any reservation for a given room with number %s doesn't exist".formatted(roomNumber));
            throw new ReservationException("Any reservation for a given condition doesn't exist");
        }
    }
}
