package wethinkcode.manager;

import wethinkcode.places.PlacesService;
import wethinkcode.schedule.ScheduleService;
import wethinkcode.service.Service;
import wethinkcode.stage.StageService;
import wethinkcode.web.WebService;

import java.util.ArrayList;
import java.util.HashMap;


public class ServiceManager extends Service{
    public static final ServiceManager SERVICE = new ServiceManager();
    static final ArrayList<Service> services = new ArrayList<>(){{
        add(PlacesService.SERVICE);
        add(StageService.SERVICE);
        add(ScheduleService.SERVICE);
        add(WebService.SERVICE);
    }};

    public final HashMap<Integer, Service> ports = new HashMap<>();

    public void startAllServices(){
        int port = Integer.parseInt(properties.get("starting_port"));

        for (Service service : services){
            service
                .initialise("-o=false", "-p="+port)
                .activate(service.getClass().getSimpleName());

            ports.put(port, service);
            port++;
        }
    }

    @Override
    protected void customServiceInitialisation() {
        startAllServices();
    }

    public static void main(String[] args) {
        SERVICE.initialise(args).activate("Service-Manager");
    }
}