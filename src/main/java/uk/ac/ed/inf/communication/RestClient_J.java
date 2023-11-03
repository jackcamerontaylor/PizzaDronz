package uk.ac.ed.inf.communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.*;

import java.net.URL;
import java.io.IOException;
import java.time.LocalDate;
import java.util.spi.LocaleNameProvider;

public class RestClient_J {

    // NEED TO GENERALISE BASE URL
    String baseURL = "https://ilp-rest.azurewebsites.net";
    private static final ObjectMapper mapper = new ObjectMapper();
    private Class<Restaurant[]> restaurantArrayClass = Restaurant[].class;

    static {
        // Configure the ObjectMapper to use the jackson-datatype-jsr310 module
        mapper.registerModule(new JavaTimeModule());
    }

    // CHECK DATE IS A STRING
    public Order[] getOrdersFromServer(String date) {
        try {
            LocalDate orderDate = LocalDate.parse(date);
            URL ordersURL = new URL(baseURL + "/orders/" + orderDate);
            return mapper.readValue(ordersURL, Order[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Check this
        return null;
    }

    public Restaurant[] getRestaurantsFromServer() {
        try{
            URL restaurantsURL = new URL(baseURL + "/restaurants");
            return mapper.readValue(restaurantsURL, Restaurant[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public NamedRegion getCentralAreaFromServer() {
        try{
            URL centralAreaURL = new URL(baseURL + "/centralArea");
            return mapper.readValue(centralAreaURL, NamedRegion.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public NamedRegion[] getNoFlyZonesFromServer() {
        try{
            URL noFlyZonesURL = new URL(baseURL + "/noFlyZones");
            return mapper.readValue(noFlyZonesURL, NamedRegion[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
