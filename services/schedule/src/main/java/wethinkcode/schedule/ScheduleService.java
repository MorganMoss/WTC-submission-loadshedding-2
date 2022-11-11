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
    @CommandLine.Option(
            names = {"-m", "--manager"},
            description = {"The URL of the manager service."}
    )
    static String manager;

    @CommandLine.Option(
            names = {"-pl", "--places"},
            description = {"The URL of the places service."}
    )
    static String places;

    public static String placeURL(){
        return (manager == null) ? places :
        Unirest.get(manager + "/service/PlacesService").asObject(String.class).getBody();
    }

    /**
     * This service uses Jackson over the default GSON
     */
    @Service.CustomJSONMapper
    public JsonMapper createJsonMapper() {
        return new JavalinJackson();
    }

    public static void main( String[] args ) {
        new Service<>(new ScheduleService()).execute(args);
    }
}
