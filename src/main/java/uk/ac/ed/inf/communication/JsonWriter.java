package uk.ac.ed.inf.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.LineString;
import org.geojson.LngLatAlt;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


public class JsonWriter {
    private static final ObjectMapper mapper = new ObjectMapper();
    // CAN YOU USE LOCALDATESERIALIZER ?
    static {
        // Configure the ObjectMapper to use the jackson-datatype-jsr310 module
        mapper.registerModule(new JavaTimeModule());
    }
    public static void writeDeliveriesToFile(Order[] orders, String orderDate) {
        try {
            String deliveriesFileName = "deliveries-" + orderDate + ".json";
            File deliveriesJsonFile = new File(deliveriesFileName);
            mapper.writeValue(deliveriesJsonFile, orders);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void recordFlightPathsToJson(List<LngLat> flightPath, String orderDate) {
        try {
            String flightPathJsonFileName = "flightpath-" + orderDate + ".json";
            File flightPathJsonFile = new File(flightPathJsonFileName);
            mapper.writeValue(flightPathJsonFile, flightPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void recordFlightPathsToGeoJson(List<LngLat> flightPath, String orderDate) {

        // Create a LineString
        LineString lineString = new LineString();
        for (LngLat point : flightPath) {
            lineString.add(new LngLatAlt(point.lng(), point.lat()));
        }

        // Create a Feature and add the LineString
        Feature feature = new Feature();
        feature.setGeometry(lineString);

        // Create a FeatureCollection and add the Feature
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.add(feature);

        // Write GeoJSON to a file
        File geoJsonFile = new File("drone-" + orderDate + ".geojson");
        try {
            mapper.writeValue(geoJsonFile, featureCollection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;

//        try {
//            String flightPathGeoJsonFileName = "drone-" + orderDate + ".geojson";
//            File flightPathGeoJsonFile = new File(flightPathGeoJsonFileName);
//            mapper.writeValue(flightPathGeoJsonFile, flightPath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
