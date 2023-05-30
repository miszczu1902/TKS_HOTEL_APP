package rabbit.factory;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import util.LoadConfig;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MQConnectionFactory {
    public static Connection createConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(LoadConfig.loadPropertyFromConfig("rabbit.hostname"));
        try (Connection connection = factory.newConnection()) {
            return connection;
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
