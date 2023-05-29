package rest.factories;

import org.apache.commons.lang3.RandomStringUtils;
import rest.dto.CreateUserDto;

public class HotelObjectsFactory {
    public static CreateUserDto createUserToAdd() {
        return new CreateUserDto(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomNumeric(2),
                RandomStringUtils.randomNumeric(2) + "-"
                        + RandomStringUtils.randomNumeric(3)
        );
    }

}
