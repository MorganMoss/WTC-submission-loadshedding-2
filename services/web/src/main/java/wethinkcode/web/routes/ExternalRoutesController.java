package wethinkcode.web.routes;

import io.javalin.apibuilder.EndpointGroup;
import org.jetbrains.annotations.NotNull;
import wethinkcode.router.Route;

import static wethinkcode.router.Route.forward;
import static wethinkcode.helpers.Helpers.getURL;
import static wethinkcode.web.WebService.manager;

@SuppressWarnings("unused")
public class ExternalRoutesController implements Route {
    @NotNull
    @Override
    public EndpointGroup getEndPoints() {
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


    private String stageURL() {
        return getURL("StageService", manager);
    }

    private String scheduleURL() {
        return getURL("ScheduleService", manager);
    }

    private String placesURL () {
        return getURL("PlacesService", manager);
    }
}
