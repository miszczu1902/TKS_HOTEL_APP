package soap.adpater;

import domain.model.Role;
import org.junit.Test;

import javax.xml.soap.SOAPMessage;

import static org.junit.Assert.assertEquals;

public class SoapHotelAdapterTest extends AbstractSoapTest {

    @Test
    public void testGetAllUsers() throws Exception {
        int amountOfUsers = getUsers(Role.USER);

        SOAPMessage user = createUser(Role.USER);
        soapConnection.call(user, ENDPOINT_URL);

        assertEquals(amountOfUsers + 1, getUsers(Role.USER));
    }

    @Test
    public void testGetAllModerators() throws Exception {
        int amountOfMods = getUsers(Role.MODERATOR);

        SOAPMessage user = createUser(Role.MODERATOR);
        soapConnection.call(user, ENDPOINT_URL);

        assertEquals(amountOfMods + 1, getUsers(Role.MODERATOR));
    }

    @Test
    public void testGetAllAdmins() throws Exception {
        int amountOfAdmins = getUsers(Role.ADMIN);

        SOAPMessage user = createUser(Role.ADMIN);
        soapConnection.call(user, ENDPOINT_URL);

        assertEquals(amountOfAdmins + 1, getUsers(Role.ADMIN));
    }

    @Test
    public void getUserTest() throws Exception {
        assertEquals("miszczu", getUser("miszczu", Role.USER));
    }

    @Test
    public void getModeratorTest() throws Exception {
        assertEquals("miszczumod", getUser("miszczumod", Role.MODERATOR));
    }

    @Test
    public void getAdminTest() throws Exception {
        assertEquals("miszczuadmin", getUser("miszczuadmin", Role.ADMIN));
    }

}
