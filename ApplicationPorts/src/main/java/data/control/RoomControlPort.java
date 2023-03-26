package data.control;

import domain.model.room.Room;

public interface RoomControlPort {

    void add(Room element);

    void remove(Room... elements);

    void update(Room... elements);

}
