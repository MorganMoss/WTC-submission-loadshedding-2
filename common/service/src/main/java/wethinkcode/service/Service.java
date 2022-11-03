package wethinkcode.service;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.json.JsonMapper;
import org.jetbrains.annotations.NotNull;
import wethinkcode.router.Router;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Logger;
import static wethinkcode.logger.Logger.formatted;


/**
 * <p>
 * <b>Generic Javalin Based Service</b>
 * </p>
 * <p>
 * This can be extended to make Services
 * </p>
 * <br/>
 * <p>
 * <b>Vital to have a routes package as a child to the package of the child service.</b>
 * </p>
 * <p>
 * That is where you implement Route Classes to be loaded into the service.
 * Override the initialise method for when you initialise data specific to the child service.
 * </p>
 * <br/>
 * <p>
 * <b>Vital to have a default.properties file in resources</b>
 * </p>
 * <p>
 * The .properties file holds any nonstandard configuration data, and the port
 * There is a CLI argument to choose an alternate config file.
 * </p>
 */
public abstract class Service implements Runnable {
    protected Javalin server;
    protected Properties properties;

    private boolean waiting = false;
    public void start() {
        server.start(properties.port);
    }

    public void stop() {
        server.stop();
    }

    /**
     * Why not put all of this into the constructor? Well, this way makes
     * it easier (possible!) to test an instance of Service without
     * starting up all the big machinery (i.e. without calling initialise()).
     */
    public Service initialise(String ... args) throws IOException, URISyntaxException {
        properties = this.initProperties(args);
        server = this.initHttpServer();
        this.addRoutes();
        return this;
    }

    /**
     * Takes a service and runs it in a separate thread.
     * @param name of that services thread
     */
    public void activate(String name){
        waiting = true;
        Thread thread = new Thread(this);
        thread.setName(name);
        thread.start();
        while (waiting) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Starts server and optionally live commands from sys.in
     */
    @Override
    public void run(){
        start();
        waiting = false;
        if (properties.commands) {
            Scanner s = new Scanner(System.in);
            String nextLine;
            while ((nextLine = s.nextLine())!=null){
                String[] args = nextLine.split(" ");
                switch (args[0].toLowerCase()) {
                    case "quit" -> {
                        stop();
                        return;
                    }

                    case "restart" -> restart(Arrays.copyOfRange(args, 1, args.length));


                    case "help" -> System.out.println(
                        """
                        commands available:
                            'help' - list of commands
                            'quit' - close the service
                            'restart' <args> - restart this service with new config
                        """
                    );
                }
            }
        }
    }

    /**
     * Restarts this instance of the server with new CLI arguments.
     * @param args CLI arguments
     */
    protected void restart(String... args){
        stop();
        try {
            initialise(args);
        } catch (Exception e) {
            System.out.println("Failed to restart, please try again.");
            return;
        }
        start();
    }

    /**
     * Gets the routes from the routes package in the given services package
     */
    private void addRoutes(){
        Router.loadRoutes(this.getClass()).forEach(server::routes);
    }

    /**
     * <b>Override this method</b> to add extra configuration to the javalin server.
     * @param config object of the javalin server
     */
    protected void customJavalinConfig(JavalinConfig config) {}

    /**
     * Creates the javalin server.
     * By default, this just loads the JsonMapper from createJsonMapper
     * @return the Javalin server object.
     */
    private Javalin initHttpServer() {
        return Javalin.create(javalinConfig -> {
            customJavalinConfig(javalinConfig);
            javalinConfig.jsonMapper(createJsonMapper());
        });
    }

    /**
     * Handles the CLI and properties file to configure the service
     * @param args CLI arguments
     * @return A properties class
     * @throws IOException if am I/O error occurs
     */
    private Properties initProperties(String... args) throws IOException {
        return new Properties(this.getClass(), args);
    }

    /**
     * <b>Override this method</b> for a custom JsonMapper
     * <br/>
     * Use GSON for serialisation instead of Jackson by default
     * because GSON allows for serialisation of objects without noargs constructors.
     *
     * @return A JsonMapper for Javalin
     */
    protected JsonMapper createJsonMapper() {
        return new GSONMapper(this.getClass().getSimpleName());
    }

    public String url() {
        return "http://localhost:" + properties.port;
    }

    /**
     * GSON serializer as a JsonMapper
     */
    private static class GSONMapper implements JsonMapper {
        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();
        final Logger logger;

        GSONMapper(String serviceName) {
            this.logger = formatted(this.getClass().getSimpleName() + " " + serviceName);
        }

        @NotNull
        @Override
        public String toJsonString(@NotNull Object obj, @NotNull Type type) {
            logger.info("To JSON: " + obj + " of type " + type.getTypeName());
            String result = gson.toJson(obj);
            logger.info("Result: " + result);
            return result;
        }

        @NotNull
        @Override
        public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
            logger.info("From JSON : " + json + " to type " + targetType.getTypeName());
            T result = gson.fromJson(json, targetType);
            logger.info("Result: " + result.toString());
            return result;
        }
    }
}