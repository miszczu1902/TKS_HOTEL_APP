package data.repositories.control;

import model.room.Room;

public interface RoomUpdatePort {
    void update(Room... elements);

}
