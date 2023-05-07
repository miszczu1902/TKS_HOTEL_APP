package service.port.infrasturcture;


import domain.model.room.Room;

import java.util.List;

public interface RoomInfServicePort {

    List<Room> getAllRooms();

    Room getRoom(int roomNumber);

}
