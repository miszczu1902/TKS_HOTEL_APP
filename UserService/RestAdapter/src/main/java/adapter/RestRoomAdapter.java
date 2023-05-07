package adapter;

import domain.model.room.Room;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class RestRoomAdapter {

    @Inject
    private RoomInfServicePort roomInfServicePort;

    @Inject
    private RoomControlServicePort roomControlServicePort;

    public List<Room> getAllRooms() {
        return roomInfServicePort.getAllRooms();
    }

    public Room getRoom(int roomNumber) {
        return roomInfServicePort.getRoom(roomNumber);
    }

    public void addRoom(Room room) throws RoomException {
        roomControlServicePort.addRoom(room);
    }

    public void updateRoom(Room room) throws RoomException {
        roomControlServicePort.updateRoom(room);
    }

    public void removeRoom(Room room) throws RoomException {
        roomControlServicePort.removeRoom(room.getRoomNumber());
    }
}
