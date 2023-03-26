package service.port.control;

import domain.exceptions.LogicException;
import domain.model.Reservation;
import domain.model.room.Room;
import domain.model.user.User;

import java.time.LocalDate;

public interface ReservationControlServicePort {

    void reserveRoom(Reservation reservation) throws LogicException;

    void reserveRoomByClient(Room room, User user, LocalDate beginTime, LocalDate endTime) throws LogicException;

    void endReserveRoom(String reservationId) throws LogicException;

}
