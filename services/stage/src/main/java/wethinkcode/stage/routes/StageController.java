package wethinkcode.stage.routes;

import com.google.gson.JsonParseException;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import wethinkcode.BadStageException;
import wethinkcode.model.Stage;
import wethinkcode.router.Route;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;
import static java.lang.Math.round;
import static wethinkcode.stage.StageService.stage;

@SuppressWarnings("unused")
public class StageController implements Route {

    /**
     * Gets a place by name
     */
    void getStage(Context ctx){
        ctx.json(stage.json());
        ctx.status(HttpStatus.OK);
    }

    void setStage(int stageNumber) throws BadStageException {
        stage = Stage.stageFromNumber(stageNumber);
    }

    void setStage(Stage stageObj) throws NullPointerException{
        Objects.requireNonNull(stageObj);
        stage = stageObj;
    }

    void setStageLegacy(Context ctx){
        try {
            setStage((int) round((Double) ctx.bodyAsClass(HashMap.class).get("stage")));
            ctx.status(HttpStatus.OK);
        } catch (NullPointerException | ClassCastException | BadStageException e) {
            ctx.json(Arrays.deepToString(e.getStackTrace()));
            ctx.status(HttpStatus.BAD_REQUEST);
        }
    }

    void setStage(Context ctx){
        try {
            setStage(ctx.bodyAsClass(Stage.class));
            ctx.status(HttpStatus.OK);
        } catch (JsonParseException notStageJSON) {
            setStageLegacy(ctx);
        }
    }

    @NotNull
    @Override
    public EndpointGroup getEndPoints() {
        return () -> path("stage", () -> {
            get(this::getStage);
            post(this::setStage);
        });
    }
}
