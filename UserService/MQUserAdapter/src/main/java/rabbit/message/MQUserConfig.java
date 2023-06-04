package rabbit.message;

import com.rabbitmq.client.*;
import rabbit.exceptions.MQException;
import rabbit.factory.MQConnectionFactory;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
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

    public void afterCreate(@Observes @Initialized(ApplicationScoped.class) Object init) {
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
