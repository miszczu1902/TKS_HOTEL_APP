package data.control;

import domain.model.dto.RoomDto;
import domain.model.room.Room;

public interface RoomControlPort {

    void add(RoomDto element);

    void remove(Room... elements);

    void update(Room... elements);

}
