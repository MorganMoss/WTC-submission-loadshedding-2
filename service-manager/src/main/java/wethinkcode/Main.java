package wethinkcode;

import wethinkcode.places.PlacesService;
import wethinkcode.schedule.ScheduleService;
import wethinkcode.stage.StageService;
import wethinkcode.web.WebService;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    static final PlacesService PLACES_SERVICE = PlacesService.SERVICE;
    static final StageService STAGE_SERVICE = StageService.SERVICE;
    static final ScheduleService SCHEDULE_SERVICE = ScheduleService.SERVICE;
    static final WebService WEB_SERVICE = WebService.SERVICE;

    static final int PLACES_PORT = 8001;
    static final int STAGE_PORT = 8002;
    static final int SCHEDULE_PORT = 8003;
    static final int WEB_PORT = 8004;


    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        PLACES_SERVICE
                .initialise("-o=false", "-p="+PLACES_PORT)
                .activate("Places-Service");
        STAGE_SERVICE
                .initialise("-o=false", "-p="+STAGE_PORT)
                .activate("Stage-Service");
        SCHEDULE_SERVICE
                .initialise("-o=false", "-p="+SCHEDULE_PORT)
                .activate("Schedule-Service");
        WEB_SERVICE
                .initialise("-o=false", "-p="+WEB_PORT)
                .activate("Web-Service");
    }
}