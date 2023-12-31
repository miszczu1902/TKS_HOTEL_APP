package rest.mapper;

import domain.model.room.EquipmentType;
import domain.model.room.Room;
import domain.model.user.User;
import mapper.RestMapper;
import org.junit.jupiter.api.Test;
import rest.dto.CreateUserDto;
import rest.dto.RoomDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestMapperTest {

    @Test
    void roomToRoomDto() {
        Room room = new Room(123, 2, 100.0, EquipmentType.BASIC);
        RoomDto roomDto = RestMapper.roomToRoomDto(room);

        assertEquals(roomDto.getRoomNumber(), room.getRoomNumber());
        assertEquals(roomDto.getCapacity(), room.getCapacity());
        assertEquals(roomDto.getPrice(), room.getPrice(), 0.001);
        assertEquals(roomDto.getEquipmentType(), room.getEquipmentType());
    }

    @Test
    void roomDtoToRoom() {
        RoomDto roomDto = new RoomDto(123, 2, 100.0, EquipmentType.BASIC);

        Room room = RestMapper.roomDtoToRoom(roomDto);

        assertEquals(123, room.getRoomNumber());
        assertEquals(2, room.getCapacity());
        assertEquals(100.0, room.getPrice(), 0.001);
        assertEquals(EquipmentType.BASIC, room.getEquipmentType());
    }

    @Test
    void createUserDtoToUser() {
        CreateUserDto createUserDto = new CreateUserDto("john_doe");
        User user = RestMapper.createUserDtoToUser(createUserDto);

        assertEquals("john_doe", user.getUsername());
    }
}