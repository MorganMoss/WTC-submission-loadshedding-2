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
            path("provinces",
                () -> get(
                    (context) -> forwardRoute(
                        context,
                        placesURL()
                            + "/provinces")
                )
            );
            path("municipalities/{province}",
                () -> get(
                    (context) -> forwardRoute(
                        context,
                        placesURL()
                            + "/municipalities/"
                            + context.pathParam("province")
                    )
                )
            );
            path("places/municipality/{municipality}",
                () -> get(
                    (context) -> forwardRoute(
                            context,
                            placesURL()
                                + "/places/municipality/"
                                + context.pathParam("municipality")
                    )
                )
            );
            path("schedule/{province}/{place}/{stage}",
                () -> get(
                    (context) -> forwardRoute(
                            context,
                            scheduleURL()
                                + "/"
                                + context.pathParam("province")
                                + "/"
                                + context.pathParam("place")
                                + "/"
                                + context.pathParam("stage"))
                )
            );
            path("stage",
                    () -> get(
                            (context) -> forwardRoute(
                                    context,
                                    stageURL()
                                            + "/stage"
                            )
                    )
            );
        };
    }

    private String getURL(String from){
        String URL = Unirest.get(SERVICE.properties.get("manager-url") + "/service/" + from).asString().getBody();
        return URL.replace("\"", "");
    }

    private String stageURL() {
        return getURL("StageService");
    }

    private String scheduleURL() {
        return getURL("ScheduleService");
    }

    private String placesURL () {
        return getURL("PlacesService");
    }

    private void forwardRoute(Context context, String route){
        GetRequest request = Unirest.get(route);
        HttpResponse<String> response = request.asString();
        context.result(response.getBody());
        context.status(response.getStatus());
    }


}
