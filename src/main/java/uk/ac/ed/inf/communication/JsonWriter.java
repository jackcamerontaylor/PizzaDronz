package uk.ac.ed.inf.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.LineString;
import org.geojson.LngLatAlt;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateSerializer;
import uk.ac.ed.inf.pathfinding.Cell;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;


public class JsonWriter {

    // Helper function to make sure all orders are in Json Format.
    private static OrderJsonFormat[] convertToDTO(Order[] orders) {
        OrderJsonFormat[] orderDTOs = new OrderJsonFormat[orders.length];
        for (int i = 0; i < orders.length; i++) {
            Order order = orders[i];
            orderDTOs[i] = new OrderJsonFormat(
                    order.getOrderNo(),
                    order.getOrderStatus(),
                    order.getOrderValidationCode(),
                    order.getPriceTotalInPence()
            );
        }
        return orderDTOs;
    }
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String OUTPUT_DIRECTORY = "resultfiles";

    static {
        // Configure the ObjectMapper to use the jackson-datatype-jsr310 module
        mapper.registerModule(new JavaTimeModule());
    }

    /**
     * Writes deliveries information to a JSON file for a specific order date.
     *
     * @param orders    An array of Order objects.
     * @param orderDate The date for which deliveries information is recorded to file.
     */
    public static void writeDeliveriesToFile(Order[] orders, String orderDate) {
        try {
            OrderJsonFormat[] orderJsonFormats = convertToDTO(orders);
            String deliveriesFileName = "deliveries-" + orderDate + ".json";
            Path deliveriesJsonPath = FileSystems.getDefault().getPath(OUTPUT_DIRECTORY, deliveriesFileName);

            // Use Gson to serialize the objects directly
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                    .setPrettyPrinting()
                    .create();

            String json = gson.toJson(orderJsonFormats);

            // Create directories if they don't exist
            Files.createDirectories(deliveriesJsonPath.getParent());

            // Use StandardOpenOption.CREATE to create the file if it doesn't exist and overwrites it if it does.
            Files.write(deliveriesJsonPath, json.getBytes(), StandardOpenOption.CREATE);

        } catch (IOException e) {
            System.err.println("An error occurred while performing I/O operations: " + e.getLocalizedMessage());
        }
    }

    /**
     * Records flight paths information to a JSON file for a specific order date.
     *
     * @param flightPath A list of FlightPathJsonFormat objects representing the flight paths.
     * @param orderDate  The date for which flight paths information is recorded.
     */
    public static void recordFlightPathsToJson(List<FlightPathJsonFormat> flightPath, String orderDate) {
        try {
            String flightPathJsonFileName = "flightpath-" + orderDate + ".json";
            Path flightPathJsonPath = FileSystems.getDefault().getPath(OUTPUT_DIRECTORY, flightPathJsonFileName);

            // Use Gson to serialize the objects directly
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                    .setPrettyPrinting()
                    .create();

            String json = gson.toJson(flightPath);

            // Create directories if they don't exist
            Files.createDirectories(flightPathJsonPath.getParent());

            // Use StandardOpenOption.CREATE to create the file if it doesn't exist and overwrites it if it does.
            Files.write(flightPathJsonPath, json.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("An error occurred while performing I/O operations: " + e.getLocalizedMessage());
        }
    }

    /**
     * Records flight paths information to a GeoJSON file for a specific order date.
     *
     * @param flightPath A list of Cell objects representing the flight paths.
     * @param orderDate  The date for which flight paths information is recorded.
     */
    public static void recordFlightPathsToGeoJson(List<Cell> flightPath, String orderDate) {

        // Create a LineString
        LineString lineString = new LineString();
        for (Cell point : flightPath) {
            lineString.add(new LngLatAlt(point.lng(), point.lat()));
        }

        // Create a Feature and add the LineString
        Feature feature = new Feature();
        feature.setGeometry(lineString);

        // Create a FeatureCollection and add the Feature
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.add(feature);

        try {
            // Create GeoJSON file path
            String geoJsonFileName = "drone-" + orderDate + ".geojson";
            Path geoJsonPath = FileSystems.getDefault().getPath(OUTPUT_DIRECTORY, geoJsonFileName);

            // Create directories if they don't exist
            Files.createDirectories(geoJsonPath.getParent());

            // Use StandardOpenOption.CREATE to create the file if it doesn't exist and overwrites it if it does.
            mapper.writeValue(geoJsonPath.toFile(), featureCollection);
        } catch (Exception e) {
            System.err.println("An error occurred while performing I/O operations: " + e.getLocalizedMessage());
        }
    }
}