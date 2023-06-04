package rabbit.factory;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class MQConnectionFactory {
    @Inject
    @ConfigProperty(name = "rabbit.hostname", defaultValue = "rabbitmq")
    private String hostname;

    @Inject
    @ConfigProperty(name = "rabbit.port", defaultValue = "5672")
    private Integer port;

    @Inject
    @ConfigProperty(name = "rabbit.username", defaultValue = "test")
    private String username;

    @Inject
    @ConfigProperty(name = "rabbit.password", defaultValue = "test")
    private String password;

    public Connection createConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostname);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        try {
            return factory.newConnection();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
