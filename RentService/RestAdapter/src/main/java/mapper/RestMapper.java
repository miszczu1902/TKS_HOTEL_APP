package mapper;

import domain.model.Role;
import domain.model.room.Room;
import domain.model.user.User;
import rest.dto.CreateUserDto;
import rest.dto.RoomDto;

public class RestMapper {

    public static RoomDto roomToRoomDto(Room room) {
        return new RoomDto(room.getRoomNumber(), room.getCapacity(), room.getPrice(), room.getEquipmentType());
    }

    public static Room roomDtoToRoom(RoomDto roomDto) {
        return new Room(roomDto.getRoomNumber(), roomDto.getCapacity(), roomDto.getPrice(), roomDto.getEquipmentType());
    }

    public static User createUserDtoToUser(CreateUserDto user) {
        return new User(
                user.getUsername(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                Role.USER,
                true,
                user.getCity(),
                user.getStreet(),
                user.getStreetNumber(),
                user.getPostalCode());
    }

}
