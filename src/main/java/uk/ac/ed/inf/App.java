package uk.ac.ed.inf;

import uk.ac.ed.inf.communication.RestClient;
import uk.ac.ed.inf.delivery.DeliveryHandler;
import uk.ac.ed.inf.communication.JsonWriter;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * The main class of the application responsible for handling command-line arguments,
 * validating inputs, handling the delivery process and writing necessary files.
 */
public class App {

    /**
     * The main method of the application.
     * It expects two command-line arguments - a date and a URL.
     *
     * @param args Command-line arguments provided to the program.
     */
    public static void main(String[] args) {
        // Check the number of arguments
        if (args.length != 2) {
            System.err.println("Error: Invalid number of arguments. Please provide a date and a URL.");
            System.exit(1);
            return;
        }

        String date = args[0];
        String url = args[1];

        // Validate the date format
        try {
            LocalDate.parse(date); // This will throw a DateTimeParseException if the date is invalid
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format. Please provide a valid date.");
            System.exit(1);
            return;
        }

        // Validate the URL format
        try {
            new URL(url); // This will throw a MalformedURLException if the URL is invalid
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL format. Please provide a valid URL.");
            System.exit(1);
            return;
        }

        // Creating an instance of DeliveryHandler and running it
        DeliveryHandler main = new DeliveryHandler(url, date);
        main.deliverOrders();

        // Write all necessary files
        JsonWriter.writeDeliveriesToFile(main.orders, date);
        JsonWriter.recordFlightPathsToJson(main.dailyDroneMoves, date);
        JsonWriter.recordFlightPathsToGeoJson(main.dailyFlightpath, date);
    }
}