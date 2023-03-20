package data.repositories.infrastructure;

import model.room.Room;

import java.util.List;

public interface RoomFindPort {
    List<Room> find(Object... elements);
}
