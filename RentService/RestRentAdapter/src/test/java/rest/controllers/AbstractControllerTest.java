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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;
import rest.dto.GetUserDto;
import rest.dto.LoginDto;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public abstract class AbstractControllerTest {

    /* Images */
    private static final DockerImageName PAYARA_IMAGE = DockerImageName.parse("payara/server-full:5.2022.4-jdk17");
    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:latest");
    private static final DockerImageName RABBIT_IMAGE = DockerImageName.parse("bitnami/rabbitmq:latest");

    /* Network and ports */
    protected static int POSTGRES_PORT;
    protected static int PAYARA_PORT;
    protected static int RABBIT_PORT;

    private static final Logger logger = LoggerFactory.getLogger("testcontainers-config");

    /* Containers */
    @Container
    private static PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withLogConsumer(new Slf4jLogConsumer(logger))
            .withDatabaseName("nbddb")
            .withUsername("nbd")
            .withPassword("nbdpassword")
            .withExposedPorts(5432);

    @Container
    private static GenericContainer<?> PAYARA = new GenericContainer<>(PAYARA_IMAGE)
            .withLogConsumer(new Slf4jLogConsumer(logger))
            .withExposedPorts(8080)
//            .withCopyFileToContainer(
//                    MountableFile.forHostPath(Paths.get("../../RestUserAdapter/target/RestUserAdapter-1.0-SNAPSHOT.war").toAbsolutePath(), 0777),
//                    "/opt/payara/deployments/RestUserAdapter-1.0-SNAPSHOT.war")
            .withCopyFileToContainer(
                    MountableFile.forHostPath(Paths.get("target/RestRentAdapter-1.0-SNAPSHOT.war").toAbsolutePath(), 0777),
                    "/opt/payara/deployments/RestRentAdapter-1.0-SNAPSHOT.war")
            .dependsOn(POSTGRES)
            .waitingFor(Wait.forHttp("/rent/api/rooms/health-check"));


    @Container
    private static GenericContainer<?> RABBIT = new GenericContainer<>(RABBIT_IMAGE)
            .withLogConsumer(new Slf4jLogConsumer(logger))
            .withExposedPorts(5672)
            .withCopyFileToContainer(
                    MountableFile.forHostPath(Paths.get("test/resources/create_users.sh").toAbsolutePath(), 0777),
                    "/docker-entrypoint-initdb.d/create_users.sh")
            .dependsOn(PAYARA);

    @Setter
    protected static String bearerToken = "";

    protected static final LoginDto adminData = new LoginDto("miszczuadmin", "123456");
    protected static final LoginDto modData = new LoginDto("miszczumod", "123456");
    protected static final LoginDto userData = new LoginDto("miszczu", "123456");

    protected static GetUserDto user;
    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeClass
    public static void prepareRestAssured() {
        try (Network network = Network.newNetwork()) {
            POSTGRES.withNetwork(network).withNetworkAliases("databaseRent");
            PAYARA.withNetwork(network).withNetworkAliases("appserver");
            RABBIT.withNetwork(network).withNetworkAliases("rabbit");

            POSTGRES.start();
            POSTGRES_PORT = POSTGRES.getMappedPort(5432);
            PAYARA.start();
            PAYARA_PORT = PAYARA.getMappedPort(8080);
            RABBIT.start();
            RABBIT_PORT = RABBIT.getMappedPort(5672);

            logger.info("Postgres port: " + POSTGRES_PORT);
            logger.info("Payara port: " + PAYARA_PORT);
            logger.info("Rabbit port: " + PAYARA_PORT);

            RestAssured.baseURI = "http://localhost:" + PAYARA_PORT + "/rent/api";
            RestAssured.port = PAYARA_PORT;
            RestAssured.defaultParser = Parser.JSON;
            RestAssured.useRelaxedHTTPSValidation();
            mapper.registerModule(new JavaTimeModule());
            RestAssured.config = RestAssured.config().objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory(
                    (cls, charset) -> mapper
            ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void endTestAndStopContainers() {
        PAYARA.stop();
        logger.info("Payara container stopped.");
        POSTGRES.stop();
        logger.info("Postgres container stopped.");
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
