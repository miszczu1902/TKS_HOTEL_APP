package adapter;

import domain.exceptions.RoomException;
import domain.model.room.Room;
import rest.dto.RoomDto;
import service.port.control.RoomControlServicePort;
import service.port.infrasturcture.RoomInfServicePort;
import services.RoomService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;


@ApplicationScoped
public class RestRoomAdapter implements RoomInfServicePort, RoomControlServicePort {

    @Inject
    private RoomService roomService;


    public List<RoomDto> getAllRooms() {
        return roomService.getAllRooms().stream()
                .map(room -> new RoomDto(
                        room.getRoomNumber(),
                        room.getCapacity(),
                        room.getPrice(),
                        room.getEquipmentType()))
                .toList();
    }

    @Override
    public Room getRoom(int roomNumber) {
        return roomService.getRoom(roomNumber);
    }

    @Override
    public void addRoom(Room room) throws RoomException {
        roomService.addRoom(room);
    }

    @Override
    public void updateRoom(Room room) throws RoomException {
        roomService.updateRoom(room);
    }

    @Override
    public void removeRoom(Room room) throws RoomException {
        roomService.removeRoom(room.getRoomNumber());
    }
}
