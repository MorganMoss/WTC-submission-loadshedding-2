package wethinkcode.router;

import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static io.javalin.apibuilder.ApiBuilder.path;

/**
 * An interface used to create plugins for an API
 */
public interface Route{
    /**
     * This contains all the handlers for this routes
     * @return endpoint group that will to be added to a set of endpoint groups
     */
    @NotNull
    EndpointGroup getEndPoints();


    /**
     * Takes a route, assuming the same names for path parameters,
     * fills the parameters with context values and fetches that new request
     * and passes it on to the context
     * @param thisURL for this service
     * @param theirURL for the other service being forwarded
     * @param request the type of request function, i.e. get, post etc.
     *                Pass the Javalin ApiBuilder methods into here
     */
    static void forward(String thisURL, String theirURL, Consumer<Handler> request){
        path(thisURL , () -> request.accept((context) -> {
            AtomicReference<String> newURL = new AtomicReference<>(theirURL);
            Arrays
                    .stream(thisURL.split("/"))
                    .filter(s -> s.charAt(0) == '{' && s.charAt(s.length()-1) == '}')
                    .forEach(s -> newURL.set(newURL.get().replace(
                            s,
                            context.pathParam(s
                                    .replace("{","")
                                    .replace("}","")
                    ))));

            forwardRoute(context, newURL.get());
        }));
    }

    /**
     * Takes a route, assuming the same names for path parameters,
     * fills the parameters with context values and fetches that new request
     * and passes it on to the context.
     * Specifying no Consumer defaults to get()
     * @param thisURL for this service
     * @param theirURL for the other service being forwarded
     */
    static void forward(String thisURL, String theirURL) {
        forward(thisURL, theirURL, ApiBuilder::get);
    }

    static void forwardRoute(Context context, String route){
        GetRequest request = Unirest.get(route);
        HttpResponse<String> response = request.asString();
        context.result(response.getBody());
        context.status(response.getStatus());
    }
}
