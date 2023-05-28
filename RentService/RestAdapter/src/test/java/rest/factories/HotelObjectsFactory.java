package rest.factories;

import domain.model.room.EquipmentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import rest.dto.CreateUserDto;
import rest.dto.RoomDto;

import java.util.Arrays;

public class HotelObjectsFactory {

    public static RoomDto createRoomToAdd() {
        return new RoomDto(
                RandomUtils.nextInt(4, 100000),
                RandomUtils.nextInt(1, 1000),
                RandomUtils.nextDouble(1, 1000),
                Arrays.stream(EquipmentType.values()).findAny()
                        .orElse(EquipmentType.BASIC));
    }

    public static CreateUserDto createUserToAdd() {
        return new CreateUserDto(
                RandomStringUtils.randomAlphabetic(10));
    }
}
