package wethinkcode.schedule;

import java.io.IOException;
import java.net.URISyntaxException;

import io.javalin.json.JavalinJackson;
import io.javalin.json.JsonMapper;
import wethinkcode.service.Service;

/**
 * I provide a REST API providing the current loadshedding schedule for a
 * given town (in a specific province) at a given loadshedding stage.
 */
public class ScheduleService extends Service
{
    public static final ScheduleService SERVICE = new ScheduleService();


    public static void main( String[] args ) throws IOException, URISyntaxException {
        SERVICE
                .initialise()
                .activate( "Schedule-Service");
    }

    public String placeURL(){
        return properties.get("places-url"); //"http://localhost:8001";
    }

    @Override
    public Service initialise(String ... args) throws IOException, URISyntaxException {
        super.initialise(args);
        return this;
    }

    /**
     * This service uses Jackson over the default GSON
     */
    @Override
    protected JsonMapper createJsonMapper() {
        return new JavalinJackson();
    }
}
