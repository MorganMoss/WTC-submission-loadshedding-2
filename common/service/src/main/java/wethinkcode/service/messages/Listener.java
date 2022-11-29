package wethinkcode.service.messages;

import org.apache.qpid.jms.*;
import javax.jms.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static wethinkcode.logger.Logger.formatted;

public class Listener {

    private final Logger logger;
    private final String destinationName;
    private final String user;
    private final String password;
    private final String connectionURI;

    public Listener(String destinationName, String name) {
        this.logger = formatted("Listener " + name, "\u001b[38;5;9m", "\u001b[38;5;209m");

        this.destinationName = destinationName;
        this.user = env("ACTIVEMQ_USER", "admin");
        this.password = env("ACTIVEMQ_PASSWORD", "admin");

        String host = env("ACTIVEMQ_HOST", "localhost");
        int port = Integer.parseInt(env("ACTIVEMQ_PORT", "5672"));
        this.connectionURI = "amqp://" + host + ":" + port;
//        this.connectionURI = Service.MESSAGE_QUEUE_URL;
    }

    public void listen(Consumer<String> messageConsumer) {
        try {

        JmsConnectionFactory factory = new JmsConnectionFactory(connectionURI);
        Connection connection = factory.createConnection(user, password);
        logger.info("Starting Listener on " + destinationName);
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = null;
        if (destinationName.startsWith(Prefix.TOPIC.prefix)) {
            destination = session.createTopic(destinationName.substring(Prefix.TOPIC.prefix.length()));
        } else {
            destination = session.createQueue(destinationName.substring(Prefix.QUEUE.prefix.length()));
        }

        MessageConsumer consumer = session.createConsumer(destination);
        long start = System.currentTimeMillis();
        long count = 1;
            logger.info("Waiting for messages...");
            while (true) {
                Message msg = consumer.receive();
                if (msg instanceof TextMessage) {
                    String body = ((TextMessage) msg).getText();
                    logger.info("Received Message: " + "\u001b[38;5;203m" + body);
                    if ("SHUTDOWN".equals(body)) {
                        long diff = System.currentTimeMillis() - start;
                        logger.info(String.format("Received %d in %.2f seconds", count, (1.0 * diff / 1000.0)));
                        connection.close();
                        try {
                            Thread.sleep(10);
                        } catch (Exception e) {
                        }
                        System.exit(0);
                    }
                    logger.info("Processing Message...");
                    messageConsumer.accept(body);
                    logger.info("Message Processed");
                } else {
                    logger.info("Unexpected message type: " + msg.getClass());
                }
            }
        } catch (JMSException e) {
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
