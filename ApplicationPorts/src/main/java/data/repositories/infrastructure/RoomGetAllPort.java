package data.repositories.infrastructure;

import model.room.Room;

import java.util.List;

public interface RoomGetAllPort {
    List<Room> getAll();
}
