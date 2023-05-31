package rabbit.message;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import rabbit.event.UserCreatedEvent;
import rabbit.exceptions.MQException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.IOException;

@ApplicationScoped
public class MQProducer {
    @Inject
    private Channel channel;

    @Inject
    @ConfigProperty(name = "rabbit.queue.name", defaultValue = "UserQueue")
    private String queueName;

    @Inject
    @ConfigProperty(name = "user.exchange.name", defaultValue = "USER_EXCHANGE")
    private String exchangeName;

    public void produce(UserCreatedEvent event) {
        if (channel == null)
            throw new MQException("Error during initializing consumer, connection is not established");
        String message;
        try (Jsonb jsonb = JsonbBuilder.create()) {
            message = jsonb.toJson(event);
        } catch (Exception e) {
            throw new MQException("Invalid message format");
        }

        try {
            channel.basicPublish(exchangeName, queueName, null, message.getBytes());
        } catch (IOException e) {
            throw new MQException("Error during producing message, connection is not established");
        }
    }

    @PostConstruct
    private void initProducer() {
        if (channel == null)
            throw new MQException("Error during initializing producer, connection is not established");
        try {
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT, true);
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, exchangeName, queueName);
        } catch (IOException ignored) {
            throw new MQException("Error during connecting to queue");
        }
    }
}
