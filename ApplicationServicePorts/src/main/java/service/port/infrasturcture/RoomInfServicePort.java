package service.port.infrasturcture;


import domain.model.room.Room;

public interface RoomInfServicePort {

    Room getRoom(int roomNumber);

}
