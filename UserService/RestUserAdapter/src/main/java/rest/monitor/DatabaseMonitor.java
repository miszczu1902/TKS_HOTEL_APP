package rest.monitor;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.net.Socket;

@Readiness
@ApplicationScoped
public class DatabaseMonitor implements HealthCheck {
    @Inject
    @ConfigProperty(name = "db.hostname", defaultValue = "databaseUser")
    private String dbHost;
    @Inject
    @ConfigProperty(name = "db.dbPort", defaultValue = "5432")
    private int dbPort;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("UserService database connection");
        try {
            pingServer(dbHost, dbPort);
            responseBuilder.up();
        } catch (Exception e) {
            responseBuilder.down()
                    .withData("Error! ", e.getMessage());
        }
        return responseBuilder.build();
    }

    private void pingServer(String dbhost, int port) throws IOException {
        Socket socket = new Socket(dbhost, port);
        socket.close();
    }
}
