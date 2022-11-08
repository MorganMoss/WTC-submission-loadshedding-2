package wethinkcode.schedule;

import io.javalin.json.JavalinJackson;
import io.javalin.json.JsonMapper;
import kong.unirest.Unirest;
import wethinkcode.service.Service;

/**
 * I provide a REST API providing the current loadshedding schedule for a
 * given town (in a specific province) at a given loadshedding stage.
 */
public class ScheduleService extends Service
{
    public static final ScheduleService SERVICE = new ScheduleService();

    public static void main( String[] args ) {
        SERVICE.initialise(args).activate("Schedule-Service");
    }

    public String placeURL(){
        try {
            return Unirest.get(properties.get("manager-url") + "/service/PlacesService").asObject(String.class).getBody();
        } catch (RuntimeException e){
            return properties.get("places-url");
        }
    }

    /**
     * This service uses Jackson over the default GSON
     */
    @Override
    protected JsonMapper createJsonMapper() {
        return new JavalinJackson();
    }
}
