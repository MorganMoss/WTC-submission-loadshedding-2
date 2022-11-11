package wethinkcode.stage;

import wethinkcode.model.Stage;
import wethinkcode.service.Service;

/**
 * I provide a Stages Service_OLD for South Africa.
 */
@Service.AsService
 public class StageService{
    public Stage stage;

    @Service.RunOnInitialisation()
    public void customServiceInitialisation() {
        stage = Stage.STAGE0;
    }

    public static void main(String ... args){
       new Service<>(new StageService()).execute(args);
    }
}