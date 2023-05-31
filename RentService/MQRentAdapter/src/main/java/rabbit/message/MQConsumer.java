package rabbit.message;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import domain.exceptions.LogicException;
import domain.exceptions.UserException;
import domain.model.user.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import rabbit.event.UserCreatedEvent;
import rabbit.exceptions.MQException;
import service.port.control.UserRentControlServicePort;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class MQConsumer {
    @Inject
    private Channel channel;

    @Inject
    @ConfigProperty(name = "rabbit.queue.name", defaultValue = "UserQueue")
    private String queueName;

    @Inject
    private UserRentControlServicePort userControlServicePort;

    @PostConstruct
    public void initConsumer(@Observes @Initialized(ApplicationScoped.class) Object init) {
        if (channel == null)
            throw new MQException("Error during initializing producer, connection is not established");
        try {
            channel.queueDeclare(queueName, true, false, false, null);
            channel.basicConsume(queueName, true, this::deliverCallback, consumerTag -> {
            });
        } catch (IOException ignored) {
            throw new MQException("Error during connecting to queue");
        }
    }

    private void deliverCallback(String consumerTag, Delivery delivery) throws IOException {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        UserCreatedEvent clientCreatedEvent;

        try (Jsonb jsonb = JsonbBuilder.create()) {
            clientCreatedEvent = jsonb.fromJson(message, UserCreatedEvent.class);
        } catch (Exception e) {
            throw new MQException("Invalid message format");
        }

        if (clientCreatedEvent.getIsCreated()) {
            try {
                userControlServicePort.addUser(new User(clientCreatedEvent.getUsername()));
            } catch (LogicException e) {
                channel.basicReject(delivery.getEnvelope().getDeliveryTag(), true);
                throw new MQException("Error during creating client");
            }

        } else {
            try {
                userControlServicePort.updateUser(new User(clientCreatedEvent.getUsername()));
            } catch (UserException e) {
                throw new MQException("Error during updating client");
            }
        }
    }
}
