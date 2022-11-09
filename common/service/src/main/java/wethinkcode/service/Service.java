package wethinkcode.service;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.json.JsonMapper;
import picocli.CommandLine;
import wethinkcode.router.Router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static wethinkcode.service.Checks.*;
import static wethinkcode.service.Properties.populateFields;

public class Service<E> extends Thread {
    /**
     * The class annotated as a service
     */
    public final E service;
    /**
     * The javalin server used to host this service.
     */
    private Javalin server;
    /**
     * Port for the service
     */
    @CommandLine.Option(
            names = {"--port", "-p"},
            description = "The name of a directory where CSV datafiles may be found. This option overrides and data-directory setting in a configuration file.",
            type = Integer.class
    )
    public Integer port = 0;

    /**
     * Commands enables/disables
     */
    @CommandLine.Option(
            names = {"--commands", "-o"},
            description = "Enables or Disables Commands during runtime from sys.in",
            type = Boolean.class
    )
    Boolean commands = false;


    @CommandLine.Option(
            names = {"--domain", "-dom"},
            description = "The host name of the server"
    )
    String domain = "http://localhost";

    /**
     * Used for waiting
     */
    private final Object lock = new Object();
    private boolean started = false;
    private boolean stopped = false;

    public Service(E service){
        checkClassAnnotation(service.getClass());
        this.service = service;
    }

    /**
     * Run this method to create a new service from a class you have annotated with @AsService.
     * <br/><br/>
     * Use picocli's @CommandLine.Option for custom fields to have them instantiated by the Properties of this service
     * @return A Service object with an instance of your class.
     */
    public Service<E> execute(String ... args){
        Method[] methods = service.getClass().getMethods();
        initProperties(args);
        initHttpServer(methods);
        handleInitMethods(methods);
        activate();
        return this;
    }

    public void close(){
        if (stopped){
            throw new AlreadyStoppedException("This service is designed to be stopped once");
        }
        stopped = true;
        server.stop();
    }

    @Override
    public void run() {
        if (started){
            throw new AlreadyStartedException("This service is designed to be run once");
        }
        started = true;
        server.start(port);

        synchronized (lock){
            lock.notify();
        }

        if (commands) {
            startCommands();
        }
    }

    /**
     * Takes a service and runs it in a separate thread.
     */
    @SuppressWarnings("SleepWhileHoldingLock")
    private void activate(){
        this.setName(this.service.getClass().getSimpleName());
        this.start();

        try {
            synchronized (lock){
                lock.wait();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Allows accepting certain general commands from sys.in during runtime
     */
    private void startCommands(){
        Scanner s = new Scanner(System.in);
        String nextLine;
        while ((nextLine = s.nextLine())!=null) {
            String[] args = nextLine.split(" ");
            switch (args[0].toLowerCase()) {
                case "quit" -> {
                    close();
                    System.exit(0);
                    return;
                }

                case "help" -> System.out.println(
                        """
                                commands available:
                                    'help' - list of commands
                                    'quit' - close the service
                                """
                );
            }
        }
    }

    private void handleCustomJavalinConfigs(Method[] methods, JavalinConfig javalinConfig) {
        Arrays
                .stream(methods)
                .filter(method -> method.isAnnotationPresent(CustomJavalinConfig.class))
                .forEach(method -> handleCustomJavalinConfig(method, javalinConfig));
    }

    private void handleCustomJavalinConfig(Method method, JavalinConfig javalinConfig) {
        checkHasJavalinConfigAsArg(method);
        try {
            method.invoke(service, javalinConfig);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonMapper handleCustomJSONMapper(Method[] methods){
        List<Method> mapper = Arrays
                .stream(methods)
                .filter(method -> method.isAnnotationPresent(CustomJSONMapper.class))
                .toList();

        if (mapper.size() > 1){
            throw new MultipleJSONMapperMethodsException(service.getClass().getSimpleName() + " has more than one custom JSON Mapper");
        }

        if (mapper.isEmpty()){
            return createJsonMapper();
        }

        Method method = mapper.get(0);
        checkForNoArgs(method);
        checkHasReturnType(method, JsonMapper.class);
        try {
            return (JsonMapper) method.invoke(service);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Use GSON for serialisation instead of Jackson by default
     * because GSON allows for serialisation of objects without noargs constructors.
     *
     * @return A JsonMapper for Javalin
     */
    private JsonMapper createJsonMapper() {
        return new GSONMapper(this.getClass().getSimpleName());
    }

    private void handleInitMethods(Method[] methods){
        Arrays
                .stream(methods)
                .filter(method -> method.isAnnotationPresent(RunOnServiceInitialisation.class))
                .forEach(this::handleInitMethod);
    }

    private void handleInitMethod(Method method){
        checkForNoArgs(method);
        try {
            method.invoke(service);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Handles the CLI and properties file to configure the service
     *
     * @param args CLI arguments
     */
    private void initProperties(String... args) {
        populateFields(this, service, args);
    }

    /**
     * Gets the routes from the routes package in the given services package
     */
    private void addRoutes(){
        Router.loadRoutes(service.getClass()).forEach(server::routes);
    }


    /**
     * Creates the javalin server.
     * By default, this just loads the JsonMapper from createJsonMapper
     */
    private void initHttpServer(Method[] methods) {
        server = Javalin.create(
            javalinConfig -> {
                handleCustomJavalinConfigs(methods, javalinConfig);
                javalinConfig.jsonMapper(handleCustomJSONMapper(methods));
            }
        );

        this.addRoutes();
    }

    public String url() {
        return domain + ":" + port;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface AsService {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface CustomJavalinConfig {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface CustomJSONMapper {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface RunOnServiceInitialisation {}
}

/**
 * Contains All the Special Checks that throw exceptions during execution of a service
 */
class Checks {
    static void checkForNoArgs(Method method){
        if (method.getGenericParameterTypes().length != 0) {
            throw new MethodTakesNoArgumentsException(method.getName() + " must have no arguments");
        }
    }

    /**
     * Checks if a method has the correct return type.
     * @param method checked
     * @param type the method should return
     * @throws BadReturnTypeException if not correct
     */
    static void checkHasReturnType(Method method, Class<?> type) {
        if (!method.getReturnType().equals(type)){
            throw new BadReturnTypeException(
                    method.getName() + " has a bad return type. \n"
                            + "Expected: " +  type.getTypeName() + "\n"
                            + "Actual: " + method.getReturnType().getSimpleName()
            );
        }
    }

    static void checkClassAnnotation(Class<?> clazz){
        if (!clazz.isAnnotationPresent(Service.AsService.class)){
            throw new NotAServiceException(
                    clazz.getSimpleName() + " is not an Annotated with @AsService"
            );
        }
    }

    static void checkHasJavalinConfigAsArg(Method method){
        Type[] params = method.getGenericParameterTypes();
        if (params.length != 1){
            throw new NoJavalinConfigArgumentException(
                    method.getName() + " has no Parameters");
        }
        if (!params[0].equals(JavalinConfig.class)){
            throw new NoJavalinConfigArgumentException(
                    method.getName() + " must have JavalinConfig as it's single parameter");
        }
    }
}

class NotAServiceException extends RuntimeException {
    public NotAServiceException(String message){
        super(message);
    }
}

class MultipleJSONMapperMethodsException extends RuntimeException {
    public MultipleJSONMapperMethodsException(String message){
        super(message);
    }
}

class MethodTakesNoArgumentsException extends RuntimeException {
    public MethodTakesNoArgumentsException(String message){
        super(message);
    }
}

class NoJavalinConfigArgumentException extends RuntimeException {
    public NoJavalinConfigArgumentException(String message){
        super(message);
    }
}

class BadReturnTypeException extends RuntimeException {
    public BadReturnTypeException(String message){
        super(message);
    }
}

class AlreadyStartedException extends RuntimeException {
    public AlreadyStartedException(String message){
        super(message);
    }
}
class AlreadyStoppedException extends RuntimeException {
    public AlreadyStoppedException(String message){
        super(message);
    }
}



