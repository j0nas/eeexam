package no.jenjon13.eeexam.ejb;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.ejb.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class CountryEJB {
    public List<String> getCountries() {
        URI uri = UriBuilder
                .fromUri("http://restcountries.eu/rest/v1/all")
                .port(80)
                .build();
        Client client = ClientBuilder.newClient();
        Response response = client
                .target(uri)
                .request("application/json")
                .get();


        String countriesJson = response.readEntity(String.class);

        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = (JsonArray) jsonParser.parse(countriesJson);

        List<String> countries = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();
            JsonElement nameJsonElement = jsonObject.get("name");
            countries.add(nameJsonElement.getAsString());
        }

        return countries;
    }
}
