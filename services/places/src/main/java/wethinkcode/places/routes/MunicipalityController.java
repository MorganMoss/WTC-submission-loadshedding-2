package wethinkcode.places.routes;

import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import wethinkcode.model.Municipality;

import java.util.List;
import java.util.Optional;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

import static wethinkcode.places.PlacesService.SERVICE;

import wethinkcode.router.Route;

@SuppressWarnings("unused")
public class MunicipalityController implements Route {
    /**
     * Gets a municipality by name
     */
    void getMunicipality(Context ctx){
        String name = ctx.pathParam("name");
        Optional<Municipality> municipality = SERVICE.places.municipality(name);

        if (municipality.isPresent()){
            ctx.json(municipality.get());
            ctx.status(HttpStatus.FOUND);
        } else {
            ctx.status(HttpStatus.NOT_FOUND);
        }
    }

    void getMunicipalitiesInProvince(Context ctx){
        String province = ctx.pathParam("province");
        List<Municipality> municipalities = SERVICE.places.municipalitiesIn(province);

        if (municipalities.size()>0){
            ctx.json(municipalities);
            ctx.status(HttpStatus.FOUND);
        } else {
            ctx.status(HttpStatus.NOT_FOUND);
        }
    }

    @NotNull
    @Override
    public EndpointGroup getEndPoints() {
        return () -> {
            path("municipality", () -> path("{name}", () -> get(this::getMunicipality)));
            path("towns", () -> path("{province}", () -> get(this::getMunicipalitiesInProvince)));
            path("municipalities", () -> path("{province}", () -> get(this::getMunicipalitiesInProvince)));
        };
    }
}
