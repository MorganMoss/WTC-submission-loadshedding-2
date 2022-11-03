package wethinkcode.schedule.routes;

import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import wethinkcode.BadStageException;
import wethinkcode.model.Schedule;
import wethinkcode.model.Stage;
import wethinkcode.router.Route;
import wethinkcode.schedule.transfer.ScheduleDAO;

import java.util.Optional;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class ServiceController implements Route {
    @NotNull
    @Override
    public EndpointGroup getEndPoints() {
        return () -> path("{province}/{place}/{stage}", () -> get(this::getSchedule));
    }

    private void getSchedule(Context context) {
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

        schedule = ScheduleDAO.getSchedule(province, place, stage.stage);


        if (schedule.isPresent()){
            context.json(schedule.get());
            context.status(HttpStatus.OK);
            return;
        }

        context.json(ScheduleDAO.emptySchedule());
        context.status(HttpStatus.NOT_FOUND);

    }

}
