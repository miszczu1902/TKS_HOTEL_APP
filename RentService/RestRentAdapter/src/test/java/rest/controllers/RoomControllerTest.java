package rest.controllers;

import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import rest.dto.LoginDto;
import rest.dto.RoomDto;
import rest.factories.HotelObjectsFactory;

import java.util.List;

public class RoomControllerTest extends AbstractControllerTest {

    @Test
    public void getAllRooms() {
        Response response = sendRequestAndGetResponse(Method.GET, "/rooms", null, ContentType.JSON);
        assertEqualsCustom(200, response.getStatusCode());
    }

    @Test
    public void createRoom() {
        RoomDto roomToAdd = HotelObjectsFactory.createRoomToAdd();
        Response response = sendRequestAndGetResponse(Method.POST, "/rooms", objectToJson(roomToAdd), ContentType.JSON);

        assertEqualsCustom(201, response.getStatusCode());

        RoomDto room = sendRequestAndGetResponse(Method.GET, "/rooms/" + roomToAdd.getRoomNumber(), null, null)
                .body().as(RoomDto.class);
        assertEqualsCustom(roomToAdd, room);
    }

    @Test
    public void deleteRoom() {
        RoomDto roomToAdd = HotelObjectsFactory.createRoomToAdd();
        Response roomAddedResponse = sendRequestAndGetResponse(Method.POST, "/rooms", objectToJson(roomToAdd), ContentType.JSON);
        assertEqualsCustom(201, roomAddedResponse.getStatusCode());

        Response response = sendRequestAndGetResponse(Method.DELETE, "/rooms/" + roomToAdd.getRoomNumber(), null, null);
        assertEqualsCustom(204, response.getStatusCode());
    }

    @Test
    public void getSpecifiedRoom() {
        Integer roomNumber;
        List<RoomDto> rooms = sendRequestAndGetResponse(Method.GET, "/rooms", null, ContentType.JSON)
                .body().jsonPath().getList("", RoomDto.class);

        if (rooms.isEmpty()) {
            RoomDto roomToAdd = HotelObjectsFactory.createRoomToAdd();
            sendRequestAndGetResponse(Method.POST, "/rooms", objectToJson(roomToAdd), ContentType.JSON);
            roomNumber = sendRequestAndGetResponse(Method.GET, "/rooms", null, ContentType.JSON)
                    .body().jsonPath().getList("", RoomDto.class).get(0).getRoomNumber();
        } else {
            roomNumber = rooms.get(0).getRoomNumber();
        }

        Response response = sendRequestAndGetResponse(Method.GET, "/rooms/" + roomNumber, null, ContentType.JSON);
        assertEqualsCustom(response.getStatusCode(), 200);

    }

}
