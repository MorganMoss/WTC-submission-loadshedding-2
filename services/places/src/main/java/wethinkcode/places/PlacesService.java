package wethinkcode.places;


import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import wethinkcode.service.Service;


/**
 * I provide a Province-names Service for places in South Africa.
 * <p>
 * I read place-name data from a CSV file that I read and
 * parse into the objects (domain model) that I use,
 * discarding unwanted data in the file (things like mountain/river names). With my "database"
 * built, I then serve-up place-name data as JSON to clients.
 * <p>
 * Clients can request:
 * <ul>
 * <li>a list of available Provinces
 * <li>a list of all Towns/PlacesService in a given Province
 * <li>a list of all neighbourhoods in a given Municipality
 * </ul>
 * I understand the following command-line arguments:
 * <dl>
 * <dt>-c | --config &lt;configfile&gt;
 * <dd>a file pathname referring to an (existing!) configuration file in standard Java
 *      properties-file format
 * <dt>-d | --datadir &lt;datadirectory&gt;
 * <dd>the name of a directory where CSV datafiles may be found. This option <em>overrides</em>
 *      and data-directory setting in a configuration file.
 * <dt>-p | --places &lt;csvdatafile&gt;
 * <dd>a file pathname referring to a CSV file of place-name data. This option
 *      <em>overrides</em> any value in a configuration file and will bypass any
 *      data-directory set via command-line or configuration.
 */

public class PlacesService extends Service {

    public static final PlacesService SERVICE = new PlacesService();

    public static void main(String... args) throws IOException, URISyntaxException {
        SERVICE
                .initialise(args)
                .activate("Places-Service");
    }

    public Places places;
    @Override
    public Service initialise(String ... args) throws IOException, URISyntaxException {
        super.initialise(args);
        places = initPlacesDb();
        return this;
    }

    private Places initPlacesDb() throws URISyntaxException {

        File databaseFile;

        try {
            databaseFile = new File(Resources.getResource(properties.get("data")).toURI());
        } catch (IllegalArgumentException e) {
            databaseFile = new File(properties.get("data"));
        }


        try {
            return new PlacesCsvParser().parseCsvSource(databaseFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}