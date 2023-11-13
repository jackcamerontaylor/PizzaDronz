//package uk.ac.ed.inf;
//
//import org.junit.Test;
//import uk.ac.ed.inf.communication.RestClient_J;
//import uk.ac.ed.inf.ilp.data.NamedRegion;
//import uk.ac.ed.inf.ilp.data.LngLat;
//import uk.ac.ed.inf.pathfinding.AStarPathFinder;
//import uk.ac.ed.inf.pathfinding.Cell;
//
//import static org.junit.Assert.*;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class AStarTest {
//
//    @Test
//    public void testFindShortestPathAndVisualize() {
//        RestClient_J restClient = new RestClient_J();
//
//        //Appleton
//        Cell start = new Cell(-3.186874, 55.944494);
//
//        Cell[] goals = {
//                new Cell(-3.1838572025299072, 55.94449876875712),
//                new Cell(-3.1940174102783203, 55.94390696616939),
//        };
//
//        NamedRegion[] noFlyZones = restClient.getNoFlyZonesFromServer();
//        NamedRegion centralArea = restClient.getCentralAreaFromServer();
//
//        List<Cell> allPaths = new ArrayList<>();
//
//        for (Cell goal : goals) {
//            // Call the method you want to test
//            List<Cell> shortestPath = AStarPathFinder.findShortestPath(start, goal, noFlyZones, centralArea);
//
//            // Write assertions to check the results
//            assertNotNull(shortestPath); // Ensure the path is not null
//            assertTrue(shortestPath.size() > 0); // Check that the path contains at least one point
//
//            // Add the path to the accumulated list
//            allPaths.addAll(shortestPath);
//        }
//
//        // Save all paths as a single GeoJSON data
//        String geoJSON = generateGeoJSON(allPaths, centralArea, noFlyZones);
//
//        try (FileWriter fileWriter = new FileWriter("all_paths.geojson")) {
//            fileWriter.write(geoJSON);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private String generateGeoJSON(List<LngLat> paths, NamedRegion centralArea, NamedRegion[] noFlyZones) {
//        // Create a StringBuilder and add the GeoJSON features for all paths
//        StringBuilder geoJSON = new StringBuilder();
//        geoJSON.append("{\n");
//        geoJSON.append("  \"type\": \"FeatureCollection\",\n");
//        geoJSON.append("  \"features\": [\n");
//
//        // Add the central area as a polygon
//        addPolygonFeature(geoJSON, "Central Area", Arrays.asList(centralArea.vertices()), "green");
//
//        // Add the no-fly zones as polygons
//        for (NamedRegion noFlyZone : noFlyZones) {
//            addPolygonFeature(geoJSON, noFlyZone.name(), Arrays.asList(noFlyZone.vertices()), "red");
//        }
//
//        // Add all the paths as a single line string
//        addLineStringFeature(geoJSON, "Drone Paths", paths);
//
//        geoJSON.append("  ]\n");
//        geoJSON.append("}\n");
//
//        return geoJSON.toString();
//    }
//
//    private void addPolygonFeature(StringBuilder geoJSON, String name, List<LngLat> coordinates, String color) {
//        geoJSON.append("    {\n");
//        geoJSON.append("      \"type\": \"Feature\",\n");
//        geoJSON.append("      \"properties\": {},\n"); // An empty "properties" object
//        geoJSON.append("      \"geometry\": {\n");
//        geoJSON.append("        \"type\": \"Polygon\",\n");
//        geoJSON.append("        \"coordinates\": [\n");
//        geoJSON.append("          [\n");
//
//        for (LngLat point : coordinates) {
//            geoJSON.append("            [").append(point.lng()).append(", ").append(point.lat()).append("],\n");
//        }
//
//        // Close the polygon by repeating the first coordinate
//        LngLat firstPoint = coordinates.get(0);
//        geoJSON.append("            [").append(firstPoint.lng()).append(", ").append(firstPoint.lat()).append("]\n");
//        geoJSON.append("          ]\n");
//        geoJSON.append("        ]\n");
//        geoJSON.append("      }\n");
//        geoJSON.append("    },\n");
//    }
//
//    private void addLineStringFeature(StringBuilder geoJSON, String name, List<LngLat> coordinates) {
//        geoJSON.append("    {\n");
//        geoJSON.append("      \"type\": \"Feature\",\n");
//        geoJSON.append("      \"properties\": {},\n"); // An empty "properties" object
//        geoJSON.append("      \"geometry\": {\n");
//        geoJSON.append("        \"type\": \"LineString\",\n");
//        geoJSON.append("        \"coordinates\": [\n");
//
//        for (LngLat point : coordinates) {
//            geoJSON.append("          [").append(point.lng()).append(", ").append(point.lat()).append("],\n");
//        }
//
//        // Remove the trailing comma after the last coordinate
//        geoJSON.deleteCharAt(geoJSON.length() - 2);
//        geoJSON.append("        ]\n");
//        geoJSON.append("      }\n");
//        geoJSON.append("    },\n");
//    }
//}
//
