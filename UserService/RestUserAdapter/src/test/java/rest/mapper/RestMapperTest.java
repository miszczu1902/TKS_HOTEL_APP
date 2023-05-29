package rest.mapper;

import domain.model.Role;
import domain.model.User;
import mapper.RestMapper;
import org.junit.jupiter.api.Test;
import rest.dto.CreateUserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestMapperTest {

    @Test
    void createUserDtoToUser() {
        CreateUserDto createUserDto = new CreateUserDto("john_doe", "pass123", "John", "Doe", "New York", "Main Street", "10", "12-345");
        User user = RestMapper.createUserDtoToUser(createUserDto);

        assertEquals("john_doe", user.getUsername());
        assertEquals("pass123", user.getPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(Role.USER, user.getRole());
        assertEquals(true, user.getIsActive());
        assertEquals("New York", user.getCity());
        assertEquals("Main Street", user.getStreet());
        assertEquals("10", user.getStreetNumber());
        assertEquals("12-345", user.getPostalCode());
    }
}