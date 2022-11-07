package wethinkcode.stage;

import wethinkcode.model.Stage;
import wethinkcode.service.Service;

/**
 * I provide a Stages Service for South Africa.
 */
public class StageService extends Service {

    public static final StageService SERVICE = new StageService();

    public static void main(String ... args){
       SERVICE.initialise(args).activate("Stage-Service");
    }

    public Stage stage;

    @Override
    protected void customServiceInitialisation() {
        stage = Stage.STAGE0;
    }
}