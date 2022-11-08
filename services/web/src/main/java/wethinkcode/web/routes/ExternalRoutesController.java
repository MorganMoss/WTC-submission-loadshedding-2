package wethinkcode.web.routes;

import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import kong.unirest.*;
import org.jetbrains.annotations.NotNull;
import wethinkcode.router.Route;

import static io.javalin.apibuilder.ApiBuilder.*;
import static wethinkcode.web.WebService.SERVICE;

public class ExternalRoutesController implements Route {
    @NotNull
    @Override
    public EndpointGroup getEndPoints() {
        return () -> {
            path("service/{service}", () -> get(this::getServiceURL));
            path("provinces",
                    () -> get((context) -> forwardRoute(context, placesURL() + "/provinces")));
            path("municipalities/{province}",
                    () -> get((context) -> forwardRoute(context, placesURL() + "/municipalities/" + context.pathParam("province"))));
            path("places/municipality/{municipality}",
                    () -> get((context) -> forwardRoute(context, placesURL() + "/places/municipality/" + context.pathParam("municipality"))));
        };
    }

    private void getServiceURL(Context context) {
        String service = context.pathParam("service");
        forwardRoute(context, SERVICE.properties.get("manager-url") + "/service/" + service);
    }

    private String placesURL () {
        String placesURL = Unirest.get(SERVICE.properties.get("manager-url") + "/service/PlacesService").asString().getBody();
        System.out.println(placesURL);
        return placesURL.replace("\"", "");
    }

    private Context forwardRoute(Context context, String route){
        GetRequest request = Unirest.get(route);
        HttpResponse<String> response = request.asString();
        context.result(response.getBody());
        context.status(response.getStatus());
        return context;
    }


}
