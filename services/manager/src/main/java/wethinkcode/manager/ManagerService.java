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

import static wethinkcode.service.Properties.getDefaultPropertiesStream;

@Service.AsService
public class ManagerService {
    private static final ArrayList<Object> services = new ArrayList<>(){{
        add(new PlacesService());
        add(new StageService());
        add(new ScheduleService());
        add(new WebService());
    }};
    public static final ManagerService MANAGER_SERVICE = new ManagerService();
    private static final Service<ManagerService> SERVICE = new Service<>(MANAGER_SERVICE);
    public final HashMap<Integer, Service<Object>> ports = new HashMap<>();

    private void addToProperties(Properties properties, int port) {
        properties.setProperty("manager", SERVICE.url());
        properties.setProperty("port", String.valueOf(port));
        properties.setProperty("commands", "false");
    }
    private void setUpProperties(File f, InputStream defaults, int port){
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(defaults));
            addToProperties(properties, port + ports.size() + 1);
            properties.store(new FileOutputStream(f), f.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Service.RunOnPost(port = true)
    public void startAllServices(int port){
        Path folder;
        try {
            folder = Path.of(this.getClass().getProtectionDomain()
                    .getCodeSource()
                    .getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        for (Object service : services){
            File f =new File(folder.resolve(service.getClass().getSimpleName() + ".properties").toUri());

            setUpProperties(f, getDefaultPropertiesStream(service.getClass()), port);

            ports.put(port + ports.size() + 10, new Service<>(service).execute("-c="+f.getAbsolutePath()));
        }
    }

    public static void main(String[] args) {
        SERVICE.execute(args);
    }
}