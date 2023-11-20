package uk.ac.ed.inf;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.io.File;
import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class AppTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }


    public void testMainMethodOutput() throws IOException {
        // Define the test input parameters
        String date = "2023-09-01";
        String url = "https://ilp-rest.azurewebsites.net";

        // Define the expected output file names
        String deliveriesFileName = "drone-2023-11-15.json";
        String flightPathsJsonFileName = "flightpath-2023-11-15.json";
        String flightPathsGeoJsonFileName = "flightpath-2023-11-15.geojson";

        // Execute the main method
        App.main(new String[] { date, url });

        // Verify that the output files exist
        assertTrue(outputFileExists(deliveriesFileName));
        assertTrue(outputFileExists(flightPathsJsonFileName));
        assertTrue(outputFileExists(flightPathsGeoJsonFileName));

        // You can add further assertions to validate the content of the output files, if needed.
    }

    // Helper method to check if an output file exists
    private boolean outputFileExists(String fileName) {
        File outputFile = new File(fileName);
        return outputFile.exists();
    }
}










