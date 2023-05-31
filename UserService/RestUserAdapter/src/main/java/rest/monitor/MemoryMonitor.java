package rest.monitor;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Liveness
@ApplicationScoped
public class MemoryMonitor implements HealthCheck {
    @Inject
    @ConfigProperty(name = "memory.threshold", defaultValue = "0.9")
    private double memoryThreshold;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("UserService memory usage");
        long freeMemory = Runtime.getRuntime().freeMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();
        double memoryUsage = (double) (maxMemory - freeMemory) / maxMemory;
        if (memoryUsage <= memoryThreshold) {
            responseBuilder.up()
                    .withData("usage", "Current memory usage " + memoryUsage * 100 + "%");
        } else {
            responseBuilder.down().withData("usage",
                    "Current memory usage " + memoryUsage * 100 + " %");
        }
        return responseBuilder.build();
    }
}
