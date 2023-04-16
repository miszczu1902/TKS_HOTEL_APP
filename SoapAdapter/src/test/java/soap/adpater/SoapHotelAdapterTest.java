package soap.adpater;

import org.junit.Test;
import soap.adapters.SoapHotelAdapter;
import soap.adapters.UserService;
import soap.adapters.UserSoap;

import java.net.MalformedURLException;

public class SoapHotelAdapterTest {

    @Test
    public void testSoapAdapter() throws MalformedURLException {
        UserService userService = new UserService();
        SoapHotelAdapter soapHotelAdapter = userService.getSoapHotelAdapterPort();

        UserSoap miszczuadmin = soapHotelAdapter.getAdmin("miszczuadmin");


    }

}
