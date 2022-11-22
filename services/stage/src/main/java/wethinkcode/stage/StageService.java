package wethinkcode.stage;

import kong.unirest.json.JSONObject;
import org.eclipse.jetty.util.BlockingArrayQueue;
import wethinkcode.BadStageException;
import wethinkcode.model.Stage;
import wethinkcode.service.Listener;
import wethinkcode.service.Service;

import java.util.PriorityQueue;
import java.util.Queue;

import static wethinkcode.stage.routes.StageController.setStage;

/**
 * I provide a Stages Service_OLD for South Africa.
 */
@Service.AsService
 public class StageService{
    public Stage stage;

    @Service.Publish(destination = "stage", prefix = Listener.Prefix.TOPIC)
    public Queue<String> stageUpdates = new BlockingArrayQueue<>();

    @Service.Listen(destination = "stage", prefix = Listener.Prefix.QUEUE)
    public void stageUpdater(String message){
        int stage = new JSONObject(message).getInt("stage");
        try {
            setStage(Stage.stageFromNumber(stage), this);
        } catch (BadStageException e) {
            e.printStackTrace();
        }
    }

    @Service.RunOnInitialisation()
    public void customServiceInitialisation() {
        setStage(Stage.STAGE0, this);
    }

    public static void main(String ... args){
       new Service<>(new StageService()).execute(args);
    }
}