package data.infrastructure;

import domain.model.dto.RoomDto;
import domain.model.room.Room;

import java.util.List;

public interface RoomInfPort {

    Room get(Object element);

    List<RoomDto> getAll();

    List<RoomDto> find(Object... elements);

}
