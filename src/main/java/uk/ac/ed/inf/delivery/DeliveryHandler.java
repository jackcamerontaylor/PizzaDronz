package uk.ac.ed.inf.delivery;

import uk.ac.ed.inf.communication.FlightPathJsonFormat;
import uk.ac.ed.inf.communication.RestClient;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.pathfinding.AStarPathFinder;
import uk.ac.ed.inf.pathfinding.Cell;

import java.util.ArrayList;
import java.util.List;

/**
 * The DeliveryHandler class is responsible for managing the delivery process,
 * including validating orders, finding delivery paths, making sure the flight paths are in
 * the correct format, and updating order statuses.
 */
public class DeliveryHandler {
    private final Restaurant[] restaurants;
    public final Order[] orders;
    private final NamedRegion[] noFlyZones;
    private final NamedRegion centralArea;

    // This cell can be edited in future if you want pizzas to be returned to another building.
    // Appleton tower co-ordinates to be added to SystemConstants.
    private final Cell appletonTower = new Cell(-3.186874, 55.944494);
    public List<Cell> dailyFlightpath = new ArrayList<>();
    public List<FlightPathJsonFormat> dailyDroneMoves = new ArrayList<>();

    /**
     * Constructs a DeliveryHandler object with the specified base URL and date.
     *
     * @param baseURL the base URL for communication with the server.
     * @param date    the date for which the delivery handling is initialized.
     */
    public DeliveryHandler(String baseURL, String date) {
        // BASE URL
        RestClient restClient = new RestClient(baseURL, date);

        if (!restClient.isAlive()) {
            System.err.println("Server not alive");
            System.exit(1);
        }

        this.restaurants = restClient.getRestaurantsFromServer();
        this.orders = restClient.getOrdersFromServer();
        this.noFlyZones = restClient.getNoFlyZonesFromServer();
        this.centralArea = restClient.getCentralAreaFromServer();
    }

    /**
     * Delivers orders by validating, finding paths, and updating order statuses.
     */
    public void deliverOrders() {
        OrderValidator orderValidator = new OrderValidator();

        PathCacheManager pathCacheManager = new PathCacheManager();

        for (Order order : this.orders) {
            // Validate order first
            orderValidator.validateOrder(order, this.restaurants);

            // Check when order is Valid_but_not delivered
            if (order.getOrderStatus() == (OrderStatus.VALID_BUT_NOT_DELIVERED)) {
                // Get order restaurant
                Restaurant orderRestaurant = orderValidator.findOrderRestaurant(order, this.restaurants);
                Cell orderRestaurantCell = new Cell(orderRestaurant.location().lng(), orderRestaurant.location().lat());

                List<Cell> path;
                List<Cell> pathBack;

                // Check if the path is already cached
                List<Cell> cachedPath = pathCacheManager.getVisitedPathFromCache(this.appletonTower, orderRestaurantCell, false);
                if (cachedPath == null) {
                    path = AStarPathFinder.findShortestPath(this.appletonTower, orderRestaurantCell, this.noFlyZones, this.centralArea);
                    pathCacheManager.addVisitedPathToCache(this.appletonTower, orderRestaurantCell, path, false);
                } else {
                    path = cachedPath;
                }

                // Check if the path is already cached
                List<Cell> cachedBackPath = pathCacheManager.getVisitedPathFromCache(orderRestaurantCell, this.appletonTower, true);
                if (cachedBackPath == null) {
                    pathBack = AStarPathFinder.findShortestPath(orderRestaurantCell, this.appletonTower, this.noFlyZones, this.centralArea);
                    pathCacheManager.addVisitedPathToCache(orderRestaurantCell, this.appletonTower, pathBack, true);
                } else {
                    pathBack = cachedBackPath;
                }

                // Checks to make sure the paths are not null
                if (path != null) {
                    this.dailyFlightpath.addAll(path);
                } else {
                    System.err.println("Warning: 'path' is null. No path was found.");
                }
                if (pathBack != null) {
                    this.dailyFlightpath.addAll(pathBack);
                } else {
                    System.err.println("Warning: 'pathBack' is null. No path was found.");
                }


                // Loop through the moves of the path and add them to a list in the correct JSON format.
                for (int i = 1; i < path.size(); i++) {
                    Cell cell = path.get(i);
                    FlightPathJsonFormat x = new FlightPathJsonFormat(order.getOrderNo(), cell.parentLngLat(), cell.angle(), cell.currentLngLat());
                    this.dailyDroneMoves.add(x);
                }
                // Hover movement added.
                this.dailyDroneMoves.add(new FlightPathJsonFormat(order.getOrderNo(), orderRestaurantCell.currentLngLat(), 999, orderRestaurantCell.currentLngLat()));
                // Adding path back in the correct JSON format.
                for (int i = 1; i < pathBack.size(); i++) {
                    Cell cell = pathBack.get(i);
                    FlightPathJsonFormat x = new FlightPathJsonFormat(order.getOrderNo(), cell.parentLngLat(), cell.angle(), cell.currentLngLat());
                    this.dailyDroneMoves.add(x);
                }
                // Hover movement added.
                this.dailyDroneMoves.add(new FlightPathJsonFormat(order.getOrderNo(),this.appletonTower.currentLngLat(),999, this.appletonTower.currentLngLat()));

                // Update order status
                order.setOrderStatus(OrderStatus.DELIVERED);
            }
        }
    }
}
