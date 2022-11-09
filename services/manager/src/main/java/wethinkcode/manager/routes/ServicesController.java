package wethinkcode.manager.routes;

import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import wethinkcode.router.Route;
import wethinkcode.service.Service;

import java.util.Objects;
import java.util.Optional;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static wethinkcode.manager.ManagerService.SERVICE;

@SuppressWarnings("unused")
public class ServicesController implements Route {
    @NotNull
    @Override
    public EndpointGroup getEndPoints() {
        return () -> path("service", () -> {
            path("{name}", () -> get(this::getURL));
            path("{port}", () -> get(this::getService));
        });
    }
    private void getURL(Context context) {
        String name = Objects.requireNonNull(context.pathParam("name"));
        Optional<Integer> find = SERVICE.ports
                .keySet()
                .stream()
                .filter((port) -> SERVICE.ports.get(port).getClass().getSimpleName().equals(name))
                .findFirst();

        if (find.isPresent()) {
            context.json(SERVICE.ports.get(find.get()).url());
            context.status(HttpStatus.OK);
            return;
        }

        context.status(HttpStatus.NOT_FOUND);
    }

    private void getService(Context context) {
        int port = Integer.parseInt(Objects.requireNonNull(context.pathParam("port")));
        Service service = SERVICE.ports.get(port);

        if (service != null) {
            context.json(service.getClass().getSimpleName());
            context.status(HttpStatus.OK);
            return;
        }

        context.status(HttpStatus.NOT_FOUND);
    }
}
