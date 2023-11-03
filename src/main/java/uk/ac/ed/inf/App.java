package uk.ac.ed.inf;


import uk.ac.ed.inf.delivery.DeliveryHandler;
import uk.ac.ed.inf.communication.JsonWriter;

public class App
{
    public static void main(String[] args) {
        String date = args[0];
        String url = args[1];

        DeliveryHandler main = new DeliveryHandler(url, date);

        main.deliverOrders();

        JsonWriter.writeDeliveriesToFile(main.orders, date);
        JsonWriter.recordFlightPathsToJson(main.dailyFlightpath, date);
        JsonWriter.recordFlightPathsToGeoJson(main.dailyFlightpath, date);

    }
}

