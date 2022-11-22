package wethinkcode.schedule.routes;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import kong.unirest.Unirest;
import wethinkcode.BadStageException;
import wethinkcode.model.Schedule;
import wethinkcode.model.Stage;
import wethinkcode.router.Controllers;
import wethinkcode.router.Verb;
import wethinkcode.schedule.ScheduleService;

import java.util.HashMap;
import java.util.Optional;

import static java.lang.Math.round;

@Controllers.Controller("{province}/{place}")
@SuppressWarnings("unused")
public class ScheduleController{
    @Controllers.Mapping(Verb.GET)
    public static void getSchedule(Context context, ScheduleService instance) {
        String province = context.pathParam("province");
        String place = context.pathParam("place");

        Optional<Schedule> schedule;

        schedule = instance.scheduleDAO.getSchedule(province, place, instance.getStage());


        if (schedule.isPresent()){
            context.json(schedule.get());
            context.status(HttpStatus.OK);
            return;
        }

        context.json(instance.scheduleDAO.emptySchedule());
        context.status(HttpStatus.NOT_FOUND);

    }

}
