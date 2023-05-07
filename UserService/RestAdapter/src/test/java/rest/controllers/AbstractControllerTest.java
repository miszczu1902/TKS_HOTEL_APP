package rest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Method;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Setter;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.dto.GetUserDto;
import rest.dto.LoginDto;

import java.util.Optional;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract sealed class AbstractControllerTest permits ReservationControllerTest, RoomControllerTest, UserControllerTest {

    @Setter
    protected static String bearerToken = "";

    protected static final LoginDto adminData = new LoginDto("miszczuadmin", "123456");
    protected static final LoginDto modData = new LoginDto("miszczumod", "123456");
    protected static final LoginDto userData = new LoginDto("miszczu", "123456");

    protected static GetUserDto user;
    protected static Logger logger = LoggerFactory.getLogger("testcontainers-config");
    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeClass
    public static void prepareRestAssured() {
        RestAssured.baseURI = "http://localhost:8080/hotel/api";
        RestAssured.port = 8080;
        RestAssured.defaultParser = Parser.JSON;
        mapper.registerModule(new JavaTimeModule());
        RestAssured.config = RestAssured.config().objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory(
                (cls, charset) -> mapper
        ));
    }

    protected static Response sendRequestAndGetResponse(Method method, String path, String jsonBody, ContentType contentType) {
        contentType = contentType == null ? ContentType.ANY : contentType;
        RequestSpecification request = given().contentType(contentType);

        if (jsonBody != null) request.body(jsonBody);
        if (!bearerToken.equals("") && !path.equals("/auth/login"))
            request.header(new Header("Authorization", "Bearer " + bearerToken));

        logBeforeRequest(method, path, jsonBody, contentType);
        Response response = request.request(method, baseURI + path);

        logAfterRequest(response);
        return response;
    }

    protected static String objectToJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void auth(LoginDto loginData) {
        bearerToken = sendRequestAndGetResponse(Method.POST, "/auth/login", objectToJson(loginData), ContentType.JSON)
                .getBody().asString();
        user = sendRequestAndGetResponse(Method.GET, "/users/" + loginData.getUsername(), null, null)
                .getBody()
                .as(GetUserDto.class);
    }

    private static void logMetaData(Method method, String path, ContentType contentType) {
        String logData = "Method: " + method.toString() + "\n" +
                "Path: " + baseURI + path + "\n" +
                "Content-Type: " + Optional.ofNullable(contentType.toString()).orElse("") + "\n" +
                "Authorization: " + bearerToken + "\n";
        logger.info(logData);
    }

    protected static void logBeforeRequest(Method method, String path, String jsonBody, ContentType contentType) {
        logMetaData(method, path, contentType);
        logger.info("Request body: \n" + jsonBody);
    }

    protected static void logAfterRequest(Response response) {
        logger.info("Status code: " + response.getStatusCode() + "\n" +
                "Response body: \n" + response.getBody().asPrettyString() + "\n");
    }

    protected static void assertEqualsCustom(Object expected, Object actual) {
        if ((expected == "" || expected == null) && (actual == "" || actual == null)) {
            assertTrue(true);
        } else {
            assertEquals(expected, actual);
        }
    }

}
