package wethinkcode.schedule.transfer;

import kong.unirest.*;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import wethinkcode.model.Day;
import wethinkcode.model.Province;
import wethinkcode.model.Schedule;
import wethinkcode.model.Slot;
import wethinkcode.schedule.BadProvinceNameException;

import java.time.LocalTime;
import java.util.*;

import static wethinkcode.schedule.ScheduleService.SERVICE;

public class ScheduleDAO {
    // There *must* be a better way than this...
    // See Steps 4 and 5 (the optional ones!) in the course notes.
    public static Optional<Schedule> getSchedule(String province, String place, int stage) throws BadProvinceNameException {
        validateProvince(province);
//        validatePlace(place);
//        validateStage(stage);


        return province.equalsIgnoreCase( "Mars" )
                ? Optional.empty()
                : Optional.of( mockSchedule() );
    }

    private static void validateProvince(String province) throws BadProvinceNameException {
        JSONObject toMatch = new JSONObject(new Province(province));

        JSONArray provinces = Unirest
                .get(SERVICE.placeURL() + "/provinces")
                .asJson()
                .getBody()
                .getArray();

        if (!provinces.toList().contains(toMatch)) {
            throw new BadProvinceNameException();
        }
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
