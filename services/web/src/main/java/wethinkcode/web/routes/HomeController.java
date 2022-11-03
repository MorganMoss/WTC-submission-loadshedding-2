package wethinkcode.web.routes;

import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import wethinkcode.router.Route;



import static io.javalin.apibuilder.ApiBuilder.*;

public class HomeController implements Route {
    @NotNull
    @Override
    public EndpointGroup getEndPoints() {
        return () -> {};
    }
}
