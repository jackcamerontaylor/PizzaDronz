package uk.ac.ed.inf.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.*;

import java.net.URL;
import java.io.IOException;
import java.time.LocalDate;

public class RestClient {

    // NEED TO GENERALISE BASE URL
    String baseURL;
    private final String date;
    private static final ObjectMapper mapper = new ObjectMapper();
    private final Class<Restaurant[]> restaurantArrayClass = Restaurant[].class;

    static {
        // Configure the ObjectMapper to use the jackson-datatype-jsr310 module
        mapper.registerModule(new JavaTimeModule());
    }

    // Constructor with base URL and date parameters
    public RestClient(String baseURL, String date) {
        this.baseURL = baseURL;
        this.date = date;
    }

    // CHECK DATE IS A STRING
    public Order[] getOrdersFromServer() {
        try {
            LocalDate orderDate = LocalDate.parse(this.date);
            URL ordersURL = new URL(this.baseURL + "/orders/" + orderDate);
            return mapper.readValue(ordersURL, Order[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Check this
        return null;
    }

    public Restaurant[] getRestaurantsFromServer() {
        try{
            URL restaurantsURL = new URL(this.baseURL + "/restaurants");
            return mapper.readValue(restaurantsURL, Restaurant[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public NamedRegion getCentralAreaFromServer() {
        try{
            URL centralAreaURL = new URL(this.baseURL + "/centralArea");
            return mapper.readValue(centralAreaURL, NamedRegion.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public NamedRegion[] getNoFlyZonesFromServer() {
        try{
            URL noFlyZonesURL = new URL(this.baseURL + "/noFlyZones");
            return mapper.readValue(noFlyZonesURL, NamedRegion[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
