package wethinkcode.service;

import org.apache.qpid.jms.JmsConnectionFactory;

import javax.jms.*;
import java.util.Queue;
import java.util.logging.Logger;

import static wethinkcode.logger.Logger.formatted;

public class Publisher {

    private final Logger logger;
    private final String destinationName;
    private final String user;
    private final String password;
    private final String connectionURI;

    public Publisher(String destinationName, String name) {
        this.logger = formatted("Listener " + name, "\u001b[38;5;9m", "\u001b[38;5;209m");

        this.destinationName = destinationName;
        this.user = env("ACTIVEMQ_USER", "admin");
        this.password = env("ACTIVEMQ_PASSWORD", "admin");

        String host = env("ACTIVEMQ_HOST", "localhost");
        int port = Integer.parseInt(env("ACTIVEMQ_PORT", "5672"));
        this.connectionURI = "amqp://" + host + ":" + port;
    }

    public void publish(Queue<String> messages) {
        try {

            JmsConnectionFactory factory = new JmsConnectionFactory(connectionURI);
            Connection connection = factory.createConnection(user, password);
            logger.info("Starting Publisher on " + destinationName);
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = null;
        if (destinationName.startsWith(Listener.Prefix.TOPIC.prefix)) {
            destination = session.createTopic(destinationName.substring(Listener.Prefix.TOPIC.prefix.length()));
        } else {
            destination = session.createQueue(destinationName.substring(Listener.Prefix.QUEUE.prefix.length()));
        }
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            while (true){
                if (messages.isEmpty()){
                    Thread.sleep(10);
                    continue;
                }
                String message = messages.remove();
                producer.send(session.createTextMessage(message));
                logger.info("Sent Message: " + "\u001b[38;5;203m" + message);
            }
        } catch (JMSException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String env(String key, String defaultValue) {
        String rc = System.getenv(key);
        if (rc == null)
            return defaultValue;
        return rc;
    }
}
