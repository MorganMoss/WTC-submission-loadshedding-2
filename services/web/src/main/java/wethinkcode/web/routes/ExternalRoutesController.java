package wethinkcode.web.routes;

import io.javalin.apibuilder.EndpointGroup;
import wethinkcode.router.Controllers;
import wethinkcode.web.WebService;


import static wethinkcode.helpers.Helpers.forward;
import static wethinkcode.helpers.Helpers.getURL;
import static wethinkcode.web.WebService.manager;


@Controllers.Controller("")
@SuppressWarnings("unused")
public class ExternalRoutesController{

    @Controllers.Endpoint
    static public EndpointGroup forwardedPoints(WebService instance) {
        return () -> {
            forward("provinces",
                placesURL() + "/provinces");
            forward("municipalities/{province}",
                    placesURL() + "/municipalities/{province}");
            forward("places/municipality/{municipality}",
                    placesURL() + "/places/municipality/{municipality}");
            forward("schedule/{province}/{place}/{stage}",
                    scheduleURL() + "/{province}/{place}/{stage}");
            forward("stage",
                    stageURL() + "/stage");
        };
    }

    private static String stageURL() {
        return getURL("StageService", manager);
    }

    private static String scheduleURL() {
        return getURL("ScheduleService", manager);
    }

    private static String placesURL() {
        return getURL("PlacesService", manager);
    }
}
