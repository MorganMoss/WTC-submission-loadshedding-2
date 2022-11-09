package wethinkcode.places.routes;

import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import wethinkcode.model.Place;

import java.util.List;
import java.util.Optional;

import static io.javalin.apibuilder.ApiBuilder.*;
import static wethinkcode.places.PlacesService.places;

import wethinkcode.router.Route;

@SuppressWarnings("unused")
public class PlaceController implements Route {

    /**
     * Gets a place by name
     */
    void getPlace(Context ctx){
        String name = ctx.pathParam("name");
        Optional<Place> place = places.place(name);

        if (place.isPresent()){
            ctx.json(place.get());
            ctx.status(HttpStatus.FOUND);
        } else {
            ctx.status(HttpStatus.NOT_FOUND);
        }
    }

    void getPlacesInProvince(Context ctx){
        String province = ctx.pathParam("province");
        List<Place> placeList = places.placesInProvince(province);

        if (placeList.size()>0){
            ctx.json(placeList);
            ctx.status(HttpStatus.FOUND);
        } else {
            ctx.status(HttpStatus.NOT_FOUND);
        }
    }

    void getPlacesInMunicipality(Context ctx){
        String municipality = ctx.pathParam("municipality");
        List<Place> placesList = places.placesInMunicipality(municipality);

        if (placesList.size()>0){
            ctx.json(placesList);
            ctx.status(HttpStatus.FOUND);
        } else {
            ctx.status(HttpStatus.NOT_FOUND);
        }
    }

    private void placeExists(Context context) {
        String province = context.pathParam("province");
        if (places
                .provinces()
                .stream()
                .noneMatch(p -> p.name().equals(province))
        ) {
            context.status(HttpStatus.NOT_FOUND);
            context.json("Province does not exist: " + province);
            return;
        }

        String place = context.pathParam("place");

        if (places
                .placesInProvince(province)
                .stream()
                .noneMatch(p -> p.name().equals(place))
        ) {
            context.status(HttpStatus.NOT_FOUND);
            context.json("Place does not exist in province: " + province);
            return;
        }

        context.status(HttpStatus.FOUND);


    }

    @NotNull
    @Override
    public EndpointGroup getEndPoints() {
        return () -> {
            path("place", () -> path("{name}", () -> get(this::getPlace)));
            path("places", () -> {
                path("province/{province}", () -> get(this::getPlacesInProvince));
                path("municipality/{municipality}", () -> get(this::getPlacesInMunicipality));
            });
            path("exists/{province}/{place}", () -> get(this::placeExists));
        };
    }


}
