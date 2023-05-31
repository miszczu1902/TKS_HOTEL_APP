package rest.monitor;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Startup;

import javax.enterprise.context.ApplicationScoped;

@Startup
@ApplicationScoped
public class StartServiceMonitor implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("UserService application started!").up().build();
    }
}
