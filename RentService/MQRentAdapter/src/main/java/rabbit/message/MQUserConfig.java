package rabbit.message;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import rabbit.exceptions.MQException;
import rabbit.factory.MQConnectionFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class MQUserConfig {
    @Inject
    private MQConnectionFactory connectionFactory;

    private Connection connection;

    @Produces
    public Channel getChannel() throws IOException {
        if (connection == null) {
            throw new MQException("Cannot get channel. Connection with RabbitMQ is not established");
        }
        return connection.createChannel();
    }

    @PostConstruct
    public void afterCreate() {
        connection = connectionFactory.createConnection();
    }

    @PreDestroy
    void beforeDestroy() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException ignored) {
                throw new MQException("Error during closing connection with RabbitMQ");
            }
        }
    }

}
