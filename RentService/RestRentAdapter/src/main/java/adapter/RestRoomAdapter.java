package adapter;

import domain.exceptions.RoomException;
import domain.model.room.Room;
import service.port.control.RoomRentControlServicePort;
import service.port.infrasturcture.RoomRentInfServicePort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class RestRoomAdapter {

    @Inject
    private RoomRentInfServicePort roomRentInfServicePort;

    @Inject
    private RoomRentControlServicePort roomRentControlServicePort;

    public List<Room> getAllRooms() {
        return roomRentInfServicePort.getAllRooms();
    }

    public Room getRoom(int roomNumber) {
        return roomRentInfServicePort.getRoom(roomNumber);
    }

    public void addRoom(Room room) throws RoomException {
        roomRentControlServicePort.addRoom(room);
    }

    public void updateRoom(Room room) throws RoomException {
        roomRentControlServicePort.updateRoom(room);
    }

    public void removeRoom(Room room) throws RoomException {
        roomRentControlServicePort.removeRoom(room.getRoomNumber());
    }
}
