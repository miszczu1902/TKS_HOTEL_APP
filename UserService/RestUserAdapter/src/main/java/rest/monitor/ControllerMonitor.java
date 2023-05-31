package rest.monitor;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Readiness
@ApplicationScoped
public class ControllerMonitor implements HealthCheck  {
    @Inject
    @ConfigProperty(name = "user.hostname", defaultValue = "localhost")
    private String userServiceHost;

    @Inject
    @ConfigProperty(name = "user.port", defaultValue = "8080")
    private int userServicePort;

    @Inject
    @ConfigProperty(name = "user.path", defaultValue = "/user/api/users/health-check")
    private String userServicePath;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("UserService connection");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://%s:%s%s"
                            .formatted(userServiceHost, userServicePort, userServicePath)))
                    .GET()
                    .build();
            HttpResponse<String> response =
                    HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == Response.Status.OK.getStatusCode()) {
                responseBuilder.up();
            } else {
                responseBuilder.down();
            }
        } catch (Exception e) {
            responseBuilder.down()
                    .withData("error", e.getMessage());
        }
        return responseBuilder.build();
    }
}
