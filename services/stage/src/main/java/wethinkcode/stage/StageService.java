package wethinkcode.stage;

import wethinkcode.model.Stage;
import wethinkcode.service.Service;

/**
 * I provide a Stages Service_OLD for South Africa.
 */
@Service.AsService
 public class StageService{
    public static Stage stage;

    public static void main(String ... args){
       new Service<>(new StageService()).execute(args);
    }

    @Service.RunOnInitialisation(port = false)
    public   void customServiceInitialisation() {
        stage = Stage.STAGE0;
    }
}