package uk.ac.ed.inf;


import uk.ac.ed.inf.delivery.DeliveryHandler;
import uk.ac.ed.inf.communication.JsonWriter;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class App
{
    public static void main(String[] args) {
        // Check the number of arguments
        if (args.length != 2) {
            System.err.println("Usage: java YourClass <date> <url>");
            return;
        }

        String date = args[0];
        String url = args[1];

        // Validate the date format
        try {
            LocalDate.parse(date); // This will throw a DateTimeParseException if the date is invalid
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format. Please provide a valid date.");
            return;
        }

        // Validate the URL format
        try {
            new URL(url); // This will throw a MalformedURLException if the URL is invalid
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL format. Please provide a valid URL.");
            return;
        }

        DeliveryHandler main = new DeliveryHandler(url, date);

        main.deliverOrders();

        JsonWriter.writeDeliveriesToFile(main.orders, date);
        JsonWriter.recordFlightPathsToJson(main.dailyDroneMoves, date);
        JsonWriter.recordFlightPathsToGeoJson(main.dailyFlightpath, date);

    }
}

