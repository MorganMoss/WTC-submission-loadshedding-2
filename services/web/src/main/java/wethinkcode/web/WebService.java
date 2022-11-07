package wethinkcode.web;

import io.javalin.config.JavalinConfig;
import wethinkcode.service.Service;

/**
 * I am the front-end web server for the LightSched project.
 * <p>
 * Remember that we're not terribly interested in the web front-end part of this
 * server, more in the way it communicates and interacts with the back-end
 * services.
 */
public class WebService extends Service
{
    public static final WebService SERVICE = new WebService();

    public static void main( String[] args){
        SERVICE.initialise(args).activate("Web-Service");
    }

    @Override
    protected void customJavalinConfig(JavalinConfig config) {
        config.staticFiles.add("/public");
    }
}
