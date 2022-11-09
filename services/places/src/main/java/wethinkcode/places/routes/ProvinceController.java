package wethinkcode.places.routes;

import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import wethinkcode.model.Province;

import java.util.Collection;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static wethinkcode.places.PlacesService.places;


import wethinkcode.router.Route;

@SuppressWarnings("unused")
public class ProvinceController implements Route {
    /**
     * Gets a municipality by name
     */
    void getAllProvinces(Context ctx){
        Collection<Province> provinces = places.provinces();

            ctx.json(provinces);
            ctx.status(HttpStatus.OK);
    }

    @NotNull
    @Override
    public EndpointGroup getEndPoints() {
        return () -> path("provinces", () -> get(this::getAllProvinces));
    }
}
