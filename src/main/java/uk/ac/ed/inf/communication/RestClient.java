package uk.ac.ed.inf.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.*;

import java.net.URL;
import java.io.IOException;
import java.time.LocalDate;

/**
 * A class for interacting with a remote server to retrieve information related to restaurant orders, restaurants,
 * central areas, and no-fly zones.
 */
public class RestClient {

    private final String baseURL;
    private final String date;
    private static final ObjectMapper mapper = new ObjectMapper();
    private final Class<Restaurant[]> restaurantArrayClass = Restaurant[].class;

    static {
        // Configure the ObjectMapper to use the jackson-datatype-jsr310 module
        mapper.registerModule(new JavaTimeModule());
    }

    /**
     * Constructs a RestClient with the specified base URL and date.
     *
     * @param baseURL The base URL of the remote server.
     * @param date    The date for which orders need to be analysed.
     */
    public RestClient(String baseURL, String date) {
        this.baseURL = baseURL;
        this.date = date;
    }

    /**
     * Retrieves orders from the remote server for the specified date.
     *
     * @return An array of Order objects.
     */
    public Order[] getOrdersFromServer() {
        try {
            LocalDate orderDate = LocalDate.parse(this.date);
            URL ordersURL = new URL(this.baseURL + "/orders/" + orderDate);
            return mapper.readValue(ordersURL, Order[].class);
        } catch (NullPointerException e) {
            System.err.println("NullPointerException occurred: No orders were found from the server.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("An error occurred while performing I/O operations: " + e.getLocalizedMessage());
            System.exit(1);
        }
        return null;
    }

    /**
     * Retrieves restaurants from the remote server.
     *
     * @return An array of Restaurant objects.
     */
    public Restaurant[] getRestaurantsFromServer() {
        try{
            URL restaurantsURL = new URL(this.baseURL + "/restaurants");
            return mapper.readValue(restaurantsURL, Restaurant[].class);
        } catch (NullPointerException e) {
            System.err.println("NullPointerException occurred: No restaurants were found from the server.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("An error occurred while performing I/O operations: " + e.getLocalizedMessage());
            System.exit(1);
        }
        return null;
    }

    /**
     * Retrieves the central area information from the remote server.
     *
     * @return A NamedRegion object representing the central area.
     */
    public NamedRegion getCentralAreaFromServer() {
        try{
            URL centralAreaURL = new URL(this.baseURL + "/centralArea");
            return mapper.readValue(centralAreaURL, NamedRegion.class);
        } catch (NullPointerException e) {
            System.err.println("NullPointerException occurred: Central area wasn't found in the server.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("An error occurred while performing I/O operations: " + e.getLocalizedMessage());
            System.exit(1);
        }
        return null;
    }

    /**
     * Retrieves the no-fly zones from the remote server.
     *
     * @return An array of NamedRegion objects representing the no-fly zones.
     */
    public NamedRegion[] getNoFlyZonesFromServer() {
        try{
            URL noFlyZonesURL = new URL(this.baseURL + "/noFlyZones");
            return mapper.readValue(noFlyZonesURL, NamedRegion[].class);
        } catch (NullPointerException e) {
            System.err.println("NullPointerException occurred: No NoFlyZones were found from the server.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("An error occurred while performing I/O operations: " + e.getLocalizedMessage());
            System.exit(1);
        }
        return null;
    }

    /**
     * Retrieves the isAlive from the server which indicates if the server is on or not.
     *
     * @return A boolean for whether the server is alive or not.
     */
    public boolean isAlive() {
        try{
            URL isAliveURL = new URL(this.baseURL + "/isAlive");
            return mapper.readValue(isAliveURL, boolean.class);
        } catch (NullPointerException e) {
            System.err.println("NullPointerException occurred: No isAlive boolean was found from the server.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("An error occurred while performing I/O operations: " + e.getLocalizedMessage());
            System.exit(1);
        }
        return false;
    }
}
