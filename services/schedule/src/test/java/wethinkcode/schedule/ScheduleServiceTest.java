package wethinkcode.schedule;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.*;
import wethinkcode.model.Schedule;
import wethinkcode.places.PlacesService;
import wethinkcode.service.Service;
import wethinkcode.stage.StageService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScheduleServiceTest
{
    private static Service<PlacesService> places;
    private static Service<ScheduleService> schedule;
    private static Service<StageService> stage;



    @BeforeAll
    static void startPlacesService() throws IOException {
        places = new Service<>(new PlacesService()).execute("-o=false", "-p=3222");
        stage = new Service<>(new StageService()).execute("-o=false", "-p=1234");

        File config = new File("test.properties");
        config.deleteOnExit();
        //noinspection ResultOfMethodCallIgnored
        config.delete();
        if (config.createNewFile()){
           try(FileWriter r = new FileWriter(config)) {
               r.write("places=http://localhost:3222");
           }
        }

        schedule = new Service<>(new ScheduleService()).execute("-o=false", "-c="+config.getAbsolutePath());
    }

    @AfterAll
    static void closeAll() {
        stage.close();
        schedule.close();
        places.close();
    }

    @Test
    public void testSchedule_someTown() {
        final Optional<Schedule> scheduleOptional = schedule.instance.scheduleDAO.getSchedule( "Eastern Cape", "Gqeberha", 4 );
        assertTrue( scheduleOptional.isPresent() );
        assertEquals( 4, scheduleOptional.get().numberOfDays() );
    }

    @Test
    public void testSchedule_nonexistentTown() {
        final Optional<Schedule> scheduleOptional = schedule.instance.scheduleDAO.getSchedule( "Mars", "Elonsburg", 2 );
        assertTrue( scheduleOptional.isEmpty() );
    }
}
