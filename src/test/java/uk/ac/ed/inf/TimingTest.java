package uk.ac.ed.inf;

import junit.framework.TestCase;
import uk.ac.ed.inf.delivery.DeliveryHandler;

/**
 * This class contains a JUnit test case for evaluating the timing performance of the DeliveryHandler class.
 * The test checks if the execution time of the deliverOrders method is below 60 seconds.
 */
public class TimingTest extends TestCase {

    /**
     * Tests the timing performance of the deliverOrders method when the expected duration is under 60 seconds.
     */
        public void testUnder60() {
            String url = "https://ilp-rest.azurewebsites.net";
            String date = "2023-09-01";

            DeliveryHandler main = new DeliveryHandler(url, date);

            long startTime = System.nanoTime();
            main.deliverOrders();
            long endTime = System.nanoTime();

            long duration = (endTime - startTime) / 1000000;

            assertTrue(duration < 60000);
        }
}