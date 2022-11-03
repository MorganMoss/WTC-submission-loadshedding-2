package wethinkcode.web.routes;

import io.javalin.apibuilder.EndpointGroup;
import org.jetbrains.annotations.NotNull;
import wethinkcode.router.Route;



@SuppressWarnings("unused")
public class HomeController implements Route {
    @NotNull
    @Override
    public EndpointGroup getEndPoints() {
        return () -> {};
    }
}
