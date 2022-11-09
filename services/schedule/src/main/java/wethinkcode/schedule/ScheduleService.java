package wethinkcode.schedule;

import io.javalin.json.JavalinJackson;
import io.javalin.json.JsonMapper;
import kong.unirest.Unirest;
import picocli.CommandLine;
import wethinkcode.service.Service;

/**
 * I provide a REST API providing the current loadshedding schedule for a
 * given town (in a specific province) at a given loadshedding stage.
 */
@Service.AsService
public class ScheduleService{
    public static final ScheduleService SERVICE = new ScheduleService();

    @CommandLine.Option(
            names = {"-m", "--manager"},
            description = {"The URL of the manager service."}
    )
    String managerURL;

    @CommandLine.Option(
            names = {"-pl", "--places"},
            description = {"The URL of the places service."}
    )
    String placesURL;


    public static void main( String[] args ) {
        new Service<>(SERVICE).execute(args);
    }

    public String placeURL(){
        return (managerURL == null) ? placesURL :
        Unirest.get(managerURL + "/service/PlacesService").asObject(String.class).getBody();
    }

    /**
     * This service uses Jackson over the default GSON
     */
    @Service.CustomJSONMapper
    public JsonMapper createJsonMapper() {
        return new JavalinJackson();
    }
}
