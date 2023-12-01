package uk.ac.ed.inf;

import junit.framework.TestCase;
import uk.ac.ed.inf.communication.RestClient;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;
import uk.ac.ed.inf.pathfinding.LngLatHandler;

/**
 * This class contains JUnit test cases for the LngLatHandler class, focusing on geographical point location checks.
 */

public class LngLatTests extends TestCase {

    RestClient restClient = new RestClient("https://ilp-rest.azurewebsites.net", "2023-09-01");

    LngLat appleton = new LngLat(-3.186874, 55.944494);

    LngLat edge = new LngLat(-3.19,55.942617);

    NamedRegion centralArea = restClient.getCentralAreaFromServer();

    /**
     * Tests whether a given LngLat point is inside a specified NamedRegion.
     */
    public void testIsInRegion() {
        LngLatHandling lngLatHandler = new LngLatHandler();

        boolean result = lngLatHandler.isInRegion(appleton, centralArea);
        assertTrue(result);
    }

    /**
     * Tests whether a given LngLat point is on the edge of a specified NamedRegion.
     */
    public void testIsOnEdge() {
        LngLatHandling lngLatHandler = new LngLatHandler();

        boolean result = lngLatHandler.isInRegion(edge, centralArea);
        assertTrue(result);
    }
}