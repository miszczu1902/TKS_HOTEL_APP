package soap.mapper;

import domain.model.Role;
import domain.model.user.User;
import org.junit.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;
import soap.model.UserSoap;

import static org.junit.Assert.assertEquals;

public class SoapMapperTest {

    @Test
    public void userToUserSoapTest() {
        User user = new User(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                Role.valueOf("ADMIN"),
                true,
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                "12-345"
        );

        UserSoap userSoap = SoapMapper.userToUserSoap(user);

        assertEquals(user.getUsername(), userSoap.getUsername());
        assertEquals(user.getFirstName(), userSoap.getFirstName());
        assertEquals(user.getLastName(), userSoap.getLastName());
        assertEquals(user.getCity(), userSoap.getCity());
        assertEquals(user.getStreet(), userSoap.getStreet());
        assertEquals(user.getStreetNumber(), userSoap.getStreetNumber());
        assertEquals(user.getPostalCode(), userSoap.getPostalCode());
        assertEquals(user.getRole().toString(), userSoap.getRole());
        assertEquals(user.getIsActive(), userSoap.getIsActive());
    }

    @Test
    public void userSoapToUserTest() {
        UserSoap user = new UserSoap(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                "12-345",
                "ADMIN",
                true
        );

        User userXD = SoapMapper.userSoapToUser(user);

        assertEquals(user.getUsername(), userXD.getUsername());
        assertEquals(user.getFirstName(), userXD.getFirstName());
        assertEquals(user.getLastName(), userXD.getLastName());
        assertEquals(user.getCity(), userXD.getCity());
        assertEquals(user.getStreet(), userXD.getStreet());
        assertEquals(user.getStreetNumber(), userXD.getStreetNumber());
        assertEquals(user.getPostalCode(), userXD.getPostalCode());
        assertEquals(user.getRole(), userXD.getRole().toString());
        assertEquals(user.getIsActive(), userXD.getIsActive());
    }

}
