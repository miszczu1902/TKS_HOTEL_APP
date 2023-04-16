package soap.adpater;

import domain.model.Role;
import org.junit.Test;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SoapHotelAdapterTest extends AbstractSoapTest {

    @Test
    public void testGetAllUsers() throws SOAPException, IOException {
//        int amountOfUsers = getUsers(Role.USER);
        SOAPMessage user = createUser(Role.USER);

        SOAPMessage response = soapConnection.call(user, ENDPOINT_URL);

        assertEquals(getUsers(Role.USER), 3);
    }

    @Test
    public void testGetAllModerators() throws SOAPException {
        int amountOfMods = getUsers(Role.MODERATOR);
    }

    @Test
    public void testGetAllAdmins() throws SOAPException {
        int amountOfAdmins = getUsers(Role.ADMIN);
    }

}
