package wethinkcode.router;

import io.javalin.apibuilder.ApiBuilder;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static wethinkcode.logger.Logger.formatted;
/**
 * The Router Class uses reflection
 * to get a set of endpoints from
 * classes that implement the Routes interface.
 */
public class Router {

    private final Logger logger;
    private final String route_package;
    private final Set<EndpointGroup> endpoints;

    /**
     * The constructor creates an empty set for the endpoints and assigns the routes package.
     * @param route_package the package url that this will pull classes from
     */
    public Router(String route_package, String serviceName) {
        this.route_package = route_package;
        this.endpoints = new HashSet<>();
        logger = formatted(this.getClass().getSimpleName() + " " + serviceName, "\u001B[36m");
    }

    /**
     * Gets a list of classes that implement Route
     * @return a set of classes
     */
    private Set<Class<? extends Route>> getHandlers(){
        Reflections reflections = new Reflections(route_package);
        return reflections.getSubTypesOf(Route.class);
    }

    /**
     * Takes a routes and adds it to the servers routes
     * @param route that controls handlers over a certain path.
     */
    private void setupHandler(Route route){
        endpoints.add(route.getEndPoints());
    }

    /**
     * Creates a runnable that will add a handler to the server
     * @param handler to be added
     * @return the runnable
     */
    private Runnable createRunnable(Class<? extends Route> handler){
        return () -> {
            try {
                setupHandler(handler.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            logger.info("The plugin '" + handler.getSimpleName() + "' has been loaded from " + route_package);
        };
    }

    /**
     * Waits for all the handlers to load
     * @param pool of threads
     */
    private void waitForLoad(ExecutorService pool){
        pool.shutdown();
        try {
            if (!pool.awaitTermination(5, TimeUnit.MINUTES)){
                throw new RuntimeException("Failed to load all Routes");
            }
        } catch (InterruptedException ignored){}
    }

    /**
     * Sets up the router calls for the server, excluding the static pages
     */
    private void setupAllHandlers() {
        Set<Class<? extends Route>> handlers = getHandlers();

        if (handlers.size() == 0){
            logger.info("No routes in " + route_package);
            return;
        }

        ExecutorService pool = Executors.newFixedThreadPool(handlers.size());

        logger.info("Starting to load Routes from " + route_package + "...");

        for (Class<? extends Route> handler : handlers){
            pool.submit(createRunnable(handler));
        }

        waitForLoad(pool);

        logger.info("Found and loaded all Routes from " + route_package);
    }


    /**
     * This takes all classes in a specified packages that inherit the Route interface
     * and gets their endpoint group and adds it to a set of endpoint groups.
     * @param owner The owner of the
     * @return A set of the endpoint groups pulled from the package with the given prefix
     */
    @NotNull
    public static Set<EndpointGroup> loadRoutes(Class<?> owner) {
        Router router = new Router(owner.getPackageName() + ".routes", owner.getSimpleName());
        router.setupAllHandlers();
        return router.endpoints;
    }


}
