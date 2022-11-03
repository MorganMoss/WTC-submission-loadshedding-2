package wethinkcode.stage;

import wethinkcode.model.Stage;
import wethinkcode.service.Service;

import java.io.IOException;
import java.net.URISyntaxException;


/**
 * I provide a Stages Service for South Africa.
 */
public class StageService extends Service {

    public static final StageService SERVICE = new StageService();

    public static void main(String ... args) throws IOException, URISyntaxException {
       SERVICE
               .initialise(args)
               .activate("Stage-Service");
    }

    public Stage stage;

    @Override
    public Service initialise(String ... args) throws IOException, URISyntaxException {
        super.initialise(args);
        stage = Stage.STAGE0;
        return this;
    }
}