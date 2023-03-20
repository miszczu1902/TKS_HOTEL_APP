//package controllers;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.restassured.RestAssured;
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;
//
//import static io.restassured.RestAssured.given;
//
//public abstract class AbstractControllerTest {
//
//    protected static String URI = "http://localhost:8080/api";
//    protected static final ObjectMapper MAPPER = new ObjectMapper();
//    protected static String jsonBody;
//
//    protected static Response getResponse(String path) {
//        return given().when()
//                .get(URI + path);
//    }
//
//    protected static Response postResponse(String path) {
//        return given().contentType(ContentType.JSON)
//                .body(jsonBody)
//                .post(URI + path);
//    }
//
//    protected static void prepareRestAssured() {
//        RestAssured.baseURI = URI;
//        RestAssured.port = 80;
//    }
//
//    protected void objectToJson(Object object) {
//        try {
//            jsonBody = MAPPER.writeValueAsString(object);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//}
