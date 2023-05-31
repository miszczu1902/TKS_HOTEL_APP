package service.port.infrasturcture;


import domain.model.room.Room;

import java.util.List;

public interface RoomRentInfServicePort {

    List<Room> getAllRooms();

    Room getRoom(int roomNumber);

}
