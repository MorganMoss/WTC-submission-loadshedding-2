package wethinkcode.manager;

import wethinkcode.places.PlacesService;
import wethinkcode.schedule.ScheduleService;
import wethinkcode.service.Service;
import wethinkcode.stage.StageService;
import wethinkcode.web.WebService;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;


public class ManagerService extends Service{
    public static final ManagerService SERVICE = new ManagerService();
    static final ArrayList<Service> services = new ArrayList<>(){{
        add(PlacesService.SERVICE);
        add(StageService.SERVICE);
        add(ScheduleService.SERVICE);
        add(WebService.SERVICE);
    }};

    public final HashMap<Integer, Service> ports = new HashMap<>();
    private void addToProperties(Properties properties) {
        properties.setProperty("manager-url", this.url() );
        properties.setProperty("port", String.valueOf(this.properties.port + ports.size() + 1));
        properties.setProperty("commands", "false");
    }

    private void setUpProperties(File f, InputStream defaults){
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(defaults));
            addToProperties(properties);
            properties.store(new FileOutputStream(f), f.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startAllServices(){
        Path folder;
        try {
            folder = Path.of(this.getClass().getProtectionDomain()
                    .getCodeSource()
                    .getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        for (Service service : services){
            File f =new File(folder.resolve(service.getClass().getSimpleName() + ".properties").toUri());

            setUpProperties(f, service.getDefaultPropertiesStream());

            service
                .initialise("-c="+f.getAbsolutePath())
                .activate(service.getClass().getSimpleName());

            ports.put(this.properties.port + ports.size() + 10, service);
        }
    }

    public static void main(String[] args) {
        SERVICE.initialise(args).activate("Service-Manager");
        SERVICE.startAllServices();
    }
}