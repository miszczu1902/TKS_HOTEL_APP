package service.port.infrasturcture;

import domain.exceptions.ReservationException;
import domain.model.Reservation;
import domain.model.room.Room;

import java.util.List;
import java.util.Map;

public interface RoomInfServicePort {

    List<Room> getAllRooms();

    Room getRoom(int roomNumber);

    Map<Room, List<Reservation>> getRoomWithReservations(int roomNumber) throws ReservationException;

}
