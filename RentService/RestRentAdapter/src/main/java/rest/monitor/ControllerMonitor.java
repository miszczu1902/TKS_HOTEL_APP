package rest.monitor;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Readiness
@ApplicationScoped
public class ControllerMonitor implements HealthCheck {
    @Inject
    @ConfigProperty(name = "rent.hostname", defaultValue = "localhost")
    private String rentServiceHost;

    @Inject
    @ConfigProperty(name = "rent.port", defaultValue = "8080")
    private int rentServicePort;

    @Inject
    @ConfigProperty(name = "rent.path", defaultValue = "/user/api/reservations/health-check")
    private String rentServicePath;

    @Inject
    @ConfigProperty(name = "rent.port", defaultValue = "8080")
    private int roomServicePort;

    @Inject
    @ConfigProperty(name = "room.path", defaultValue = "/user/api/rooms/health-check")
    private String roomServicePath;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("RentService connection");
        try {
            URI rentURI = new URI("http://%s:%s%s"
                    .formatted(rentServiceHost, rentServicePort, rentServicePath));
            URI roomURI = new URI("http://%s:%s%s"
                    .formatted(rentServiceHost, roomServicePort, roomServicePath));
            checkService(responseBuilder, rentURI);
            checkService(responseBuilder, roomURI);
        } catch (Exception e) {
            responseBuilder.down()
                    .withData("error", e.getMessage());
        }
        return responseBuilder.build();
    }

    private void checkService(HealthCheckResponseBuilder responseBuilder, URI uri) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response =
                    HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == Response.Status.OK.getStatusCode()) {
                responseBuilder.up();
            } else {
                responseBuilder.down();
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
