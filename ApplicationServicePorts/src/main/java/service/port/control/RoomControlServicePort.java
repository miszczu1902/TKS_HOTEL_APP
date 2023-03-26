package service.port.control;

import domain.exceptions.RoomException;
import domain.model.room.Room;

public interface RoomControlServicePort {

    void addRoom(Room room) throws RoomException;

    void updateRoom(Room room) throws RoomException;

    void removeRoom(Room room) throws RoomException;

}
