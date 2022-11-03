package wethinkcode.places;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import org.junit.jupiter.api.*;
import wethinkcode.places.model.Place;

import static org.junit.jupiter.api.Assertions.*;
import static wethinkcode.places.PlacesService.SERVICE;

/**
 * *Functional* tests of the PlacesService.
 */
public class PlacesApiTest
{
    public static final int TEST_PORT = 7777;


    @BeforeAll
    public static void startServer() throws IOException, URISyntaxException {


        File data = new File("test.csv");
        if (!data.exists()){
            if (data.createNewFile()) {
                try (FileWriter fw = new FileWriter(data)) {
                    fw.write(PlacesTestData.HEADER);
                    fw.write(PlacesTestData.CSV_DATA);
                }
            }
            data.deleteOnExit();
        }

        File properties = new File("test.properties");
        if (!properties.exists()){
            if (properties.createNewFile()) {
                try (FileWriter fw = new FileWriter(properties)) {
                    fw.write("data="+data.getAbsolutePath());
                }
            }
            properties.deleteOnExit();
        }

        SERVICE
            .initialise("-p="+TEST_PORT, "-c="+properties.getAbsolutePath())
            .activate("Test-Places-Service");

    }

    @AfterAll
    public static void stopServer(){
        SERVICE.stop();
    }

    @Test
    public void getProvincesJson(){
        HttpResponse<JsonNode> response = Unirest.get( serverUrl() + "/provinces" ).asJson();
        JSONArray array = response.getBody().getArray();
        Set<String> provinces = Set.of("KwaZulu-Natal", "Western Cape", "Gauteng", "Northern Cape", "Free State");
        Set<String> actual = new HashSet<>();
        for (int i = 0; i < array.length(); i++){
           actual.add(array.getJSONObject(i).get("name").toString());
        }
        assertEquals(provinces, actual);
    }

    @Test
    public void getTownsInAProvince_provinceExistsInDb(){
        HttpResponse<JsonNode> response = Unirest.get( serverUrl() + "/places/province/KwaZulu-Natal").asJson();
        JSONArray array = response.getBody().getArray();
        Set<Place> places = Set.of(new Place("Amatikulu", "uMlalazi"));
        Set<Place> actual = new HashSet<>();
        for (int i = 0; i < array.length(); i++){
            actual.add(new Place(array.getJSONObject(i).get("name").toString(), array.getJSONObject(i).get("municipality").toString()));
        }
        assertEquals(places, actual);

    }

    @Test
    public void getTownsInAProvince_noSuchProvinceInDb(){
        HttpResponse<JsonNode> response = Unirest.get( serverUrl() + "/place/Oregon" ).asJson();
        assertEquals(404, response.getStatus());
    }

    private String serverUrl(){
        return "http://localhost:" + TEST_PORT;
    }
}
