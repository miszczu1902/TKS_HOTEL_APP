package rest.controllers;

import domain.model.Role;
import domain.model.User;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.Test;
import rest.dto.CreateUserDto;
import rest.dto.GetUserDto;
import rest.factories.HotelObjectsFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class UserControllerTest extends AbstractControllerTest {

    @Test
    public void getAllUsers() {
        auth(adminData);
        Response response = sendRequestAndGetResponse(Method.GET, "/users", null, null);
        assertEquals(200, response.getStatusCode());

        List<GetUserDto> allUsers = response.body().jsonPath().getList("$", GetUserDto.class);
        allUsers.forEach(user -> {
            Response responseBody = sendRequestAndGetResponse(Method.GET, "/users/" + user.getUsername(),
                    null, null);
            assertEquals(200, responseBody.getStatusCode());

            User userData = responseBody.getBody().as(User.class);
            assertEqualsCustom(user.getUsername(), userData.getUsername());
            assertEqualsCustom(user.getFirstName(), userData.getFirstName());
            assertEqualsCustom(user.getLastName(), userData.getLastName());
            assertEqualsCustom(user.getCity(), userData.getCity());
            assertEqualsCustom(user.getStreet(), userData.getStreet());
            assertEqualsCustom(user.getPostalCode(), userData.getPostalCode());
            assertEqualsCustom(Role.valueOf(user.getRole()), userData.getRole());
        });
    }

    @Test
    public void createUser() {
        AbstractControllerTest.setBearerToken("");
        CreateUserDto user = HotelObjectsFactory.createUserToAdd();

        Response response = sendRequestAndGetResponse(Method.POST, "/users", objectToJson(user), ContentType.JSON);
        assertEqualsCustom(201, response.getStatusCode());

        auth(adminData);
        GetUserDto userData = sendRequestAndGetResponse(Method.GET, "/users/" + user.getUsername(), null, null)
                .body().as(GetUserDto.class);

        assertEqualsCustom(user.getUsername(), userData.getUsername());
        assertEqualsCustom(user.getFirstName(), userData.getFirstName());
        assertEqualsCustom(user.getLastName(), userData.getLastName());
        assertEqualsCustom(user.getCity(), userData.getCity());
        assertEqualsCustom(user.getStreet(), userData.getStreet());
        assertEqualsCustom(user.getPostalCode(), userData.getPostalCode());
    }

    @Test
    public void deactivateUserAndThenActivateHim() {
        AbstractControllerTest.setBearerToken("");
        CreateUserDto userToAdd = HotelObjectsFactory.createUserToAdd();

        Response response = sendRequestAndGetResponse(Method.POST, "/users", objectToJson(userToAdd), ContentType.JSON);
        assertEqualsCustom(201, response.getStatusCode());

        auth(adminData);

        response = sendRequestAndGetResponse(Method.POST, "/users/" + userToAdd.getUsername() + "/deactivate", null, null);
        assertEqualsCustom(204, response.getStatusCode());

        response = sendRequestAndGetResponse(Method.POST, "/users/" + userToAdd.getUsername() + "/activate", null, null);
        assertEqualsCustom(204, response.getStatusCode());
    }
}
