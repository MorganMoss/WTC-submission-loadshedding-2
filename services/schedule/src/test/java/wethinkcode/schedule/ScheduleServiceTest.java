package wethinkcode.schedule;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import org.junit.jupiter.api.*;
import wethinkcode.model.Schedule;
import wethinkcode.places.PlacesService;
import wethinkcode.schedule.transfer.ScheduleDAO;
import wethinkcode.service.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static wethinkcode.schedule.ScheduleService.SERVICE;

public class ScheduleServiceTest
{
    private static Service<PlacesService> places;
    private static Service<ScheduleService> schedule;

    @BeforeAll
    static void startPlacesService() throws IOException {
        places = new Service<>(new PlacesService()).execute("-o=false", "-p=3222");

        File config = new File("test.properties");
        config.deleteOnExit();
        //noinspection ResultOfMethodCallIgnored
        config.delete();
        if (config.createNewFile()){
           try(FileWriter r = new FileWriter(config)) {
               r.write("placesURL=http://localhost:3222");
           }
        }

        schedule = new Service<>(SERVICE).execute("-o=false", "-c="+config.getAbsolutePath());
    }

    @AfterAll
    static void closeAll() {
        schedule.close();
        places.close();
    }

    @Test
    public void testSchedule_someTown() {
        final Optional<Schedule> schedule = ScheduleDAO.getSchedule( "Eastern Cape", "Gqeberha", 4 );
        assertTrue( schedule.isPresent() );
        assertEquals( 4, schedule.get().numberOfDays() );
    }

    @Test
    public void testSchedule_nonexistentTown() {
        final Optional<Schedule> schedule = ScheduleDAO.getSchedule( "Mars", "Elonsburg", 2 );
        assertTrue( schedule.isEmpty() );
    }
}
