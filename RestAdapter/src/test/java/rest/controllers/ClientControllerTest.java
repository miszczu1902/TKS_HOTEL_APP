//package controllers;
//
//import dto.ClientDto;
//import model.client.Client;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.apache.commons.lang3.RandomUtils;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class ClientControllerTest extends AbstractControllerTest {
//
//    private static ClientDto CLIENT = new ClientDto(RandomStringUtils.randomAlphabetic(10),
//            RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10));
//
//
//    @BeforeClass
//    public static void prepareDataToTest() {
//        CLIENT.setStreet(RandomStringUtils.randomAlphabetic(10));
//        CLIENT.setCityName(RandomStringUtils.randomAlphabetic(10));
//        CLIENT.setStreetNumber(String.valueOf(RandomUtils.nextInt(1, 100)));
//        CLIENT.setPostalCode(RandomUtils.nextInt(10, 99) + "-" + RandomUtils.nextInt(100, 999));
//
//        prepareRestAssured();
//    }
//
//    @Test
//    public void createClient() {
//        CLIENT.setUsername(RandomStringUtils.randomAlphabetic(10));
//        objectToJson(CLIENT);
//
//        int statusCode = postResponse("/clients").getStatusCode();
//        assertEquals(201, statusCode);
//
//        Client clientResponse = getResponse("/clients/" + CLIENT.getUsername())
//                .body()
//                .as(Client.class);
//        assertEquals(CLIENT.getUsername(), clientResponse.getUsername());
//    }
//
//    @Test
//    public void deactivateClientAndThenActivateHim() {
//        createClient();
//        String username = CLIENT.getUsername();
//
//        int statusCode = postResponse("/clients/" + username + "/deactivate").getStatusCode();
//        assertEquals(204, statusCode);
//        Client clientResponse = getResponse("/clients/" + username)
//                .body()
//                .as(Client.class);
//        assertFalse(clientResponse.getIsActive());
//
//        statusCode = postResponse("/clients/" + username + "/activate").getStatusCode();
//        assertEquals(204, statusCode);
//        clientResponse = getResponse("/clients/" + username)
//                .body()
//                .as(Client.class);
//        assertTrue(clientResponse.getIsActive());
//    }
//}
