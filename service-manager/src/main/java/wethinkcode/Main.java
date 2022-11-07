package wethinkcode;

import wethinkcode.places.PlacesService;
import wethinkcode.schedule.ScheduleService;
import wethinkcode.service.Service;
import wethinkcode.stage.StageService;
import wethinkcode.web.WebService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class Main {

    static final int STARTING_PORT = 8001;

    static final ArrayList<Class<? extends Service>> services = new ArrayList<>(){{
        add(PlacesService.class);
        add(StageService.class);
        add(ScheduleService.class);
        add(WebService.class);
    }};
    public static void startAllServices(){
        ArrayList<Integer> PORT =  new ArrayList<>();
        PORT.add(STARTING_PORT);

        for (Class<? extends Service> service : services){
            System.out.println("PORT: " + PORT.get(PORT.size()-1));
            System.out.println("CLASS: " + service.getSimpleName());
            PORT.add(PORT.get(PORT.size()-1)+1);
        }

    }


    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
//        PLACES_SERVICE
//                .initialise("-o=false", "-p="+PLACES_PORT)
//                .activate("Places-Service");
//        STAGE_SERVICE
//                .initialise("-o=false", "-p="+STAGE_PORT)
//                .activate("Stage-Service");
//        SCHEDULE_SERVICE
//                .initialise("-o=false", "-p="+SCHEDULE_PORT)
//                .activate("Schedule-Service");
//        WEB_SERVICE
//                .initialise("-o=false", "-p="+WEB_PORT)
//                .activate("Web-Service");
        startAllServices();
    }
}