package wethinkcode.router;

import io.javalin.apibuilder.EndpointGroup;
import org.jetbrains.annotations.NotNull;

/**
 * An interface used to create plugins for an API
 */
public interface Route{
    /**
     * This contains all the handlers for this routes
     * @return endpoint group that will to be added to a set of endpoint groups
     */
    @NotNull
    EndpointGroup getEndPoints();
}
