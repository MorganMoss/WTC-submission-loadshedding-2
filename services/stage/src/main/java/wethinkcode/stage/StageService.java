package wethinkcode.stage;

import wethinkcode.model.Stage;
import wethinkcode.service.Service;

/**
 * I provide a Stages Service_OLD for South Africa.
 */
@Service.AsService
 public class StageService{
    public static final StageService SERVICE = new StageService();
    public static Stage stage;

    public static void main(String ... args){
       new Service<>(SERVICE).execute(args);
    }


    @Service.RunOnServiceInitialisation
    protected void customServiceInitialisation() {
        stage = Stage.STAGE0;
    }
}