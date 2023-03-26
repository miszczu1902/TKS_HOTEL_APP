package data.infrastructure;

import domain.model.room.Room;

import java.util.List;

public interface RoomInfPort {

    Room get(Object element);

    List<Room> getAll();

    List<Room> find(Object... elements);

}
