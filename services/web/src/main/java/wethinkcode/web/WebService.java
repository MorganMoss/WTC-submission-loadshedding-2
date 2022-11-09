package wethinkcode.web;

import io.javalin.config.JavalinConfig;
import picocli.CommandLine;
import wethinkcode.service.Service;

/**
 * I am the front-end web server for the LightSched project.
 * <p>
 * Remember that we're not terribly interested in the web front-end part of this
 * server, more in the way it communicates and interacts with the back-end
 * services.
 */
@Service.AsService
public class WebService{
    public static final WebService SERVICE = new WebService();
    @CommandLine.Option(
            names = {"-m", "--manager"},
            description = {"The URL of the manager service."},
            required = true
    )
    public String managerURL;

    public static void main( String[] args){
        new Service<>(SERVICE).execute(args);
    }

    @Service.CustomJavalinConfig
    protected void customJavalinConfig(JavalinConfig config) {
        config.staticFiles.add("/public");
    }
}
