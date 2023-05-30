package rabbit.consumer;

import rabbit.exceptions.MQException;
import rabbit.queue.MQQueue;
import services.UserService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.HashMap;
import java.util.Map;

public class MQConsumer implements MessageListener {
    private MQQueue mqQueue;

    @Inject
    private UserService userService;

    @PostConstruct
    public void initialize() throws MQException {
        mqQueue = new MQQueue();
        mqQueue.startListening();
    }

    @Override
    public void onMessage(Message message) {
        try {
            // Obsługa otrzymanej wiadomości
            String messageText = message.getBody(String.class);
            System.out.println("Received message: " + messageText);
            Map<String, String> data = new HashMap<>() {{
                put("old", messageText.split(",")[0]);
                put("new", messageText.split(",")[1]);
            }};

            // Przetwarzanie otrzymanej wiadomości
            userService.processMessage(data.get("old"), data.get("new"));
        } catch (Throwable e) {
            // Obsługa błędów
           throw new MQException(e);
        }
    }
}
