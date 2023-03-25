package data.control;

import model.dto.RoomDto;
import model.room.Room;

public interface RoomControlPort {

    void add(RoomDto element);

    void remove(Room... elements);

    void update(Room... elements);

}
