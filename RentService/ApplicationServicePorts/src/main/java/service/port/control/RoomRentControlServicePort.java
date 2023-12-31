package service.port.control;

import domain.exceptions.RoomException;
import domain.model.room.Room;

public interface RoomRentControlServicePort {

    void addRoom(Room room) throws RoomException;

    void updateRoom(Room room) throws RoomException;

    void removeRoom(int roomNumber) throws RoomException;

}
