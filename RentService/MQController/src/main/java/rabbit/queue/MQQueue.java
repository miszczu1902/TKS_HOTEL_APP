package rabbit.queue;

import com.rabbitmq.client.*;
import rabbit.exceptions.MQException;
import rabbit.factory.MQConnectionFactory;
import util.LoadConfig;

public class MQQueue {
    private final static String QUEUE_NAME = LoadConfig.loadPropertyFromConfig("rabbit.queue.name");

    public void startListening() {
        // Tworzenie połączenia z serwerem RabbitMQ
        try (Connection connection = MQConnectionFactory.createConnectionFactory()) {
            // Tworzenie kanału komunikacji
            Channel channel = connection.createChannel();

            // Deklaracja kolejki
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            System.out.println(" [*] Waiting for messages. To exit, press CTRL+C");

            // Tworzenie nasłuchiwacza wiadomości
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    try {
                        String message = new String(body, "UTF-8");
                        System.out.println(" [x] Received '" + message + "'");
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            // Uruchamianie nasłuchiwacza wiadomości
            channel.basicConsume(QUEUE_NAME, true, consumer);
            System.out.println("Kolejka UserQueue została zdefiniowana.");

        } catch (Throwable e) {
            throw new MQException(e);
        }
    }
}
