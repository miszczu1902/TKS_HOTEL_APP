package adapter;

import domain.exceptions.ReservationException;
import domain.exceptions.RoomException;
import domain.model.Reservation;
import domain.model.room.Room;
import service.port.control.RoomControlServicePort;
import service.port.infrasturcture.RoomInfServicePort;
import services.ReservationService;
import services.RoomService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestRoomAdapter implements RoomInfServicePort, RoomControlServicePort {

    @Inject
    private RoomService roomService;

    @Inject
    private ReservationService reservationService;

    @Override
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @Override
    public Room getRoom(int roomNumber) {
        return roomService.getRoom(roomNumber);
    }

    @Override
    public Map<Room, List<Reservation>> getRoomWithReservations(int roomNumber) throws ReservationException {
        Map<Room, List<Reservation>> roomWithReservations = new HashMap<>();
        roomWithReservations.put(roomService.getRoom(roomNumber), reservationService.getReservationsForRoom(roomNumber));
        return roomWithReservations;
    }

    @Override
    public void addRoom(Room room) throws RoomException {
        roomService.addRoom(room);
    }

    @Override
    public void updateRoom(Room room) throws RoomException {
        roomService.updateRoom(room);
    }

    @Override
    public void removeRoom(Room room) throws RoomException {
        roomService.removeRoom(room.getRoomNumber());
    }
}
