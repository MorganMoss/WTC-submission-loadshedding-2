package wethinkcode.schedule.routes;

import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import wethinkcode.model.Schedule;
import wethinkcode.router.Route;
import wethinkcode.schedule.BadProvinceNameException;
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
        int stage;
        try {
            stage = Integer.parseInt(context.pathParam("stage"));
        } catch (NumberFormatException e){
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        Optional<Schedule> schedule;

        try {
            schedule = ScheduleDAO.getSchedule(province, place, stage);
        } catch (BadProvinceNameException e) {
            context.status(HttpStatus.BAD_REQUEST);
            context.json(e);
            return;
        }

        if (schedule.isPresent()){
            context.json(schedule.get());
            context.status(HttpStatus.OK);
            return;
        }

        context.status(HttpStatus.NOT_FOUND);



    }

}
