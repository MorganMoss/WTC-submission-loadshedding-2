package wethinkcode.schedule;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import org.junit.jupiter.api.*;
import wethinkcode.model.Schedule;
import wethinkcode.places.PlacesService;
import wethinkcode.schedule.transfer.ScheduleDAO;
import wethinkcode.service.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScheduleServiceTest
{
    static Service placesService;

    @BeforeAll
    static void startPlacesService() throws IOException, URISyntaxException {
        placesService = new PlacesService().initialise("-o=false", "-p=3221");
        placesService.activate("Places Service");

        ScheduleService.SERVICE.initialise("-o=false");
    }

    @AfterAll
    static void closeAll() {
        placesService.stop();
    }

    @Test
    public void testSchedule_someTown() {
        final Optional<Schedule> schedule = ScheduleDAO.getSchedule( "Eastern Cape", "Gqeberha", 4 );
        assertThat( schedule.isPresent() );
        assertEquals( 4, schedule.get().numberOfDays() );
    }

    @Test
    public void testSchedule_nonexistentTown() {
        final Optional<Schedule> schedule = ScheduleDAO.getSchedule( "Mars", "Elonsburg", 2 );
        assertThat( schedule.isEmpty() );
    }
}
