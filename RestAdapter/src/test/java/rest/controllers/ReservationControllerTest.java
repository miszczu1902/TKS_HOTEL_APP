package rest.controllers;

import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import rest.dto.*;
import rest.factories.HotelObjectsFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class ReservationControllerTest extends AbstractControllerTest {

    private static final List<RoomWithReservationDto> roomsWithReservations = new ArrayList<>();
    private static final List<RoomWithReservationDto> roomsWithoutReservations = new ArrayList<>();

    @Before
    public void prepareDataToTest() {
        auth(adminData);
        List<Integer> roomsNumbers = sendRequestAndGetResponse(Method.GET, "/rooms", null, null)
                .body().jsonPath().getList("roomNumber", Integer.class);

        if (!roomsNumbers.isEmpty()) {
            roomsNumbers.forEach(roomNumber -> {
                RoomWithReservationDto room = sendRequestAndGetResponse(
                        Method.GET,
                        "/rooms/" + roomNumber + "/reservations",
                        null,
                        null)
                        .body().as(RoomWithReservationDto.class);

                if (!room.getReservations().isEmpty()) roomsWithReservations.add(room);
                else roomsWithoutReservations.add(room);
            });

            if (roomsWithReservations.isEmpty()) {
                RoomDto room = HotelObjectsFactory.createRoomToAdd();
                sendRequestAndGetResponse(
                        Method.POST,
                        "/rooms",
                        objectToJson(room),
                        ContentType.JSON);

                ReservationDto reservationForClient = new ReservationDto(room.getRoomNumber(),
                        LocalDate.now().plusDays(1).toString(),
                        LocalDate.now().plusDays(5).toString(),
                        "miszczu");
                sendRequestAndGetResponse(
                        Method.POST,
                        "/reservations",
                        objectToJson(reservationForClient),
                        ContentType.JSON);

                Response roomWithReservations = sendRequestAndGetResponse(
                        Method.GET,
                        "/rooms/" + room.getRoomNumber() + "/reservations",
                        null,
                        null);

                roomsWithReservations.add(roomWithReservations.getBody().as(RoomWithReservationDto.class));
            }
        }

        roomsWithReservations.forEach(RoomWithReservationDto::reversed);
    }

    @Test
    public void createReservationForMyself() {
        LoginDto userToLogin = new LoginDto("miszczu", "123456");
        auth(userToLogin);

        Integer roomNumberToReserve = getRandomNumberOfRoomWhichCouldBeReserved();
        int daysToAdd = RandomUtils.nextInt();
        LocalDate beginTime = roomsWithReservations.get(0).getReservations().get(0).getEndTime().plusDays(daysToAdd);
        LocalDate endTime = roomsWithReservations.get(0).getReservations().get(0).getEndTime().plusDays(daysToAdd + 1);
        ReservationSelfDto reservationForMyself = new ReservationSelfDto(roomNumberToReserve,
                beginTime.toString(), endTime.toString());

        Response response = sendRequestAndGetResponse(
                Method.POST,
                "/reservations/self",
                objectToJson(reservationForMyself),
                ContentType.JSON);
        assertEqualsCustom(201, response.getStatusCode());
    }

    @Test
    public void createReservationForClient() {
        auth(adminData);

        Integer roomNumberToReserve = getRandomNumberOfRoomWhichCouldBeReserved();
        int daysToAdd = RandomUtils.nextInt();
        LocalDate beginTime = roomsWithReservations.get(0).getReservations().get(0).getEndTime().plusDays(daysToAdd);
        LocalDate endTime = roomsWithReservations.get(0).getReservations().get(0).getEndTime().plusDays(daysToAdd + 1);
        ReservationDto reservationForMyself = new ReservationDto(roomNumberToReserve,
                beginTime.toString(), endTime.toString(), "miszczu");

        Response response = sendRequestAndGetResponse(
                Method.POST,
                "/reservations",
                objectToJson(reservationForMyself),
                ContentType.JSON);
        assertEqualsCustom(201, response.getStatusCode());
    }

    private Integer getRandomNumberOfRoomWhichCouldBeReserved() {
        if(!roomsWithoutReservations.isEmpty()) {
            Collections.shuffle(roomsWithoutReservations);
            return roomsWithoutReservations.stream()
                    .map(RoomWithReservationDto::getRoomNumber)
                    .toList().get(0);
        } else {
            Collections.shuffle(roomsWithReservations);
            return roomsWithReservations.stream()
                    .filter(room -> !room.getReservations().isEmpty())
                    .toList().get(0).getRoomNumber();
        }

    }
}
