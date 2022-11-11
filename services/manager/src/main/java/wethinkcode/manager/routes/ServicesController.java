package wethinkcode.manager.routes;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import wethinkcode.manager.ManagerService;
import wethinkcode.router.Controllers;
import wethinkcode.router.Verb;

import java.util.Objects;
import java.util.Optional;

@Controllers.Controller("service")
@SuppressWarnings("unused")
public class ServicesController {
    @Controllers.Mapping(value = Verb.GET, path = "{name}")
    public static void getURL(Context context, ManagerService instance) {
        String name = Objects.requireNonNull(context.pathParam("name"));
        Optional<Integer> find = instance.ports
                .keySet()
                .stream()
                .filter((port) -> instance.ports.get(port).getInstance().getClass().getSimpleName().equals(name))
                .findFirst();

        if (find.isPresent()) {
            context.json(instance.ports.get(find.get()).url());
            context.status(HttpStatus.OK);
            return;
        }

        context.status(HttpStatus.NOT_FOUND);
    }
}
