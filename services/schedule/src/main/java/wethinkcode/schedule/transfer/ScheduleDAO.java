package wethinkcode.schedule.transfer;

import kong.unirest.*;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import wethinkcode.model.*;

import java.time.LocalTime;
import java.util.*;

import static wethinkcode.schedule.ScheduleService.placeURL;

public class ScheduleDAO {
    // There *must* be a better way than this...
    // See Steps 4 and 5 (the optional ones!) in the course notes.
    public static Optional<Schedule> getSchedule(String province, String place, int stage) {
        if (validateProvince(province)){
            return Optional.of( mockSchedule() );
        }
        return Optional.empty();
    }

    private static boolean validateProvince(String province) {
        JSONObject toMatch = new JSONObject(new Province(province));

        System.out.println(placeURL());
        JSONArray provinces = Unirest
                .get(placeURL() + "/provinces")
                .asJson()
                .getBody()
                .getArray();

        return provinces.toList().contains(toMatch);
    }

    /**
     * Answer with a hard-coded/mock Schedule.
     * @return A non-null, slightly plausible Schedule.
     */
    public static Schedule mockSchedule(){
        final List<Slot> slots = List.of(
                new Slot( LocalTime.of( 2, 0 ), LocalTime.of( 4, 0 )),
                new Slot( LocalTime.of( 10, 0 ), LocalTime.of( 12, 0 )),
                new Slot( LocalTime.of( 18, 0 ), LocalTime.of( 20, 0 ))
        );
        final List<Day> days = List.of(
                new Day( slots ),
                new Day( slots ),
                new Day( slots ),
                new Day( slots )
        );
        return new Schedule( days );
    }

    /**
     * Answer with a non-null but empty Schedule.
     * @return The empty Schedule.
     */
    public static Schedule emptySchedule(){
        final List<Slot> slots = Collections.emptyList();
        final List<Day> days = Collections.emptyList();
        return new Schedule( days );
    }
}
