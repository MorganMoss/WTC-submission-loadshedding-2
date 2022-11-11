package wethinkcode.schedule.routes;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import wethinkcode.BadStageException;
import wethinkcode.model.Schedule;
import wethinkcode.model.Stage;
import wethinkcode.router.Controllers;
import wethinkcode.router.Verb;
import wethinkcode.schedule.ScheduleService;

import java.util.Optional;

@Controllers.Controller("{province}/{place}/{stage}")
@SuppressWarnings("unused")
public class ScheduleController{
    @Controllers.Mapping(Verb.GET)
    public static void getSchedule(Context context, ScheduleService instance) {
        String province = context.pathParam("province");
        String place = context.pathParam("place");
        Stage stage;

        try {
            stage = Stage.stageFromNumber(Integer.parseInt(context.pathParam("stage")));
        } catch (NumberFormatException | BadStageException e){
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        Optional<Schedule> schedule;

        schedule = instance.scheduleDAO.getSchedule(province, place, stage.stage);


        if (schedule.isPresent()){
            context.json(schedule.get());
            context.status(HttpStatus.OK);
            return;
        }

        context.json(instance.scheduleDAO.emptySchedule());
        context.status(HttpStatus.NOT_FOUND);

    }

}
