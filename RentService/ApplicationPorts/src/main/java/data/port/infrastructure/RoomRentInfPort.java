package data.port.infrastructure;

import domain.model.room.Room;

import java.util.List;

public interface RoomRentInfPort {

    Room get(Object element);

    List<Room> getAll();

    List<Room> find(Object... elements);

}
