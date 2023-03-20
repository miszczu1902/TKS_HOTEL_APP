package data.repositories.control;

import model.room.Room;

public interface RoomRemovePort {
    void remove(Room... elements);
}
