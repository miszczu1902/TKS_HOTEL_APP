package data.infrastructure;

import model.dto.RoomDto;
import model.room.Room;

import java.util.List;

public interface RoomInfPort {

    Room get(Object element);

    List<RoomDto> getAll();

    List<RoomDto> find(Object... elements);

}
