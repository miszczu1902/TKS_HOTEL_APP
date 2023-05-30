package rabbit.message;

import com.rabbitmq.client.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import rabbit.factory.MQConnectionFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class MQUserConfig {
    @Inject
    private MQConnectionFactory connectionFactory;

    private Connection connection;

//    @Inject
//    @ConfigProperty(name = "rabbit.hostname", defaultValue = "localhost")
//    private String hostname;
//
//    @Inject
//    @ConfigProperty(name = "rabbit.port", defaultValue = "5672")
//    private Integer port;
//
//    @Inject
//    @ConfigProperty(name = "rabbit.username", defaultValue = "test")
//    private String username;
//
//    @Inject
//    @ConfigProperty(name = "rabbit.password", defaultValue = "test")
//    private String password;

    @Produces
    public Channel getChannel() throws IOException {
        if (connection == null) {
            System.out.println("Cannot get channel. Connection with RabbitMQ is not established");
            return null;
        }
        return connection.createChannel();
    }

    @PostConstruct
    public void afterCreate() {
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost(hostname);
//        factory.setPort(port);
//        factory.setUsername(username);
//        factory.setPassword(password);
//        try {
//            connection = factory.newConnection();
//        } catch (IOException | TimeoutException e) {
//            throw new RuntimeException(e);
//        }
        connection = connectionFactory.createConnection();
    }

    @PreDestroy
    void beforeDestroy() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException ignored) {
                System.out.println("Error during closing connection with RabbitMQ");
            }
        }
    }

}
