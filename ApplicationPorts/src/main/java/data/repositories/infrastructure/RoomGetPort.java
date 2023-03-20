package data.repositories.infrastructure;

import model.room.Room;

public interface RoomGetPort {
    Room get(Object element);
}
