package uk.ac.ed.inf;

import junit.framework.TestCase;
import uk.ac.ed.inf.communication.RestClient;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.pathfinding.AStarPathFinder;
import uk.ac.ed.inf.pathfinding.Cell;

import java.util.List;

/**
 * This class contains JUnit test cases for the AStarPathFinder class, testing various scenarios of pathfinding using the A* algorithm.
 */
public class AStarTests extends TestCase {

    RestClient restClient = new RestClient("https://ilp-rest.azurewebsites.net", "2023-09-01");

    //Appleton
    Cell start = new Cell(-3.186874, 55.944494);

    Cell[] goals = {
            new Cell(-3.1838572025299072, 55.94449876875712),
            new Cell(-3.1940174102783203, 55.94390696616939),
            new Cell(-3.186874, 56.5),
            new Cell(-3.1940174102783203, 55.94)
    };

    NamedRegion[] noFlyZones = restClient.getNoFlyZonesFromServer();
    NamedRegion centralArea = restClient.getCentralAreaFromServer();

    /**
     * Tests the A* algorithm for finding the shortest path in a small area.
     */
    public void testSmallPath() {

        List<Cell> path = AStarPathFinder.findShortestPath(start, goals[0], noFlyZones, centralArea);

        assertNotNull(path);
    }

    /**
     * Tests the A* algorithm for finding the shortest path in a small area.
     */
    public void testSmallPath2() {

        List<Cell> path = AStarPathFinder.findShortestPath(start, goals[1], noFlyZones, centralArea);

        assertNotNull(path);
    }

    /**
     * Tests the A* algorithm for finding the shortest path in a long distance.
     */
    public void testLongPath() {

        List<Cell> path = AStarPathFinder.findShortestPath(start, goals[2], noFlyZones, centralArea);

        assertNotNull(path);
    }

    /**
     * Tests the A* algorithm for finding the shortest path in an area with passing through a no-fly zone.
     */
    public void testNoFlyZonePath() {

        List<Cell> path = AStarPathFinder.findShortestPath(start, goals[3], noFlyZones, centralArea);

        assertNotNull(path);
    }
}
