//package controllers;
//
//import dto.RoomDto;
//import org.apache.commons.lang3.RandomUtils;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import static io.restassured.RestAssured.given;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class RoomControllerTest extends AbstractControllerTest {
//
//    private static RoomDto ROOM = new RoomDto(RandomUtils.nextInt(1, 1000), RandomUtils.nextInt(1, 1000),
//            RandomUtils.nextDouble(0.0001, 1000));
//
//    @BeforeClass
//    public static void prepareDataToTest() {
//       prepareRestAssured();
//    }
//
//    @Test
//    public void createRoom() {
//        ROOM.setRoomNumber(RandomUtils.nextInt(1, 10000));
//        objectToJson(ROOM);
//        int statusCode = postResponse("/rooms").getStatusCode();
//        assertEquals(201, statusCode);
//
//        RoomDto room = getResponse("/rooms/" + ROOM.getRoomNumber())
//                .body()
//                .as(RoomDto.class);
//        assertEquals(ROOM.getRoomNumber(), room.getRoomNumber());
//    }
//
//    @Test
//    public void deleteRoom() {
//        createRoom();
//        int roomNumber = ROOM.getRoomNumber();
//
//        int statusCode = given().delete(URI + "/rooms/" + roomNumber).getStatusCode();
//        assertEquals(204, statusCode);
//    }
//}
