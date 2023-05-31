package data.port.control;

import domain.model.room.Room;

public interface RoomRentControlPort {

    void add(Room element);

    void remove(Room... elements);

    void update(Room... elements);

}
