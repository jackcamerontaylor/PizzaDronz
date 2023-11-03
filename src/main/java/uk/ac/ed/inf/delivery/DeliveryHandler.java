package uk.ac.ed.inf.delivery;

import uk.ac.ed.inf.communication.RestClient_J;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.pathfinding.AStarPathFinder;
import uk.ac.ed.inf.pathfinding.Cell;

import java.util.ArrayList;
import java.util.List;

public class DeliveryHandler {

    private final Restaurant[] restaurants;
    public final Order[] orders;
    private final NamedRegion[] noFlyZones;
    private final NamedRegion centralArea;
    private final Cell appletonTower = new Cell(-3.186874, 55.944494);
    // WILL BE ADDED TO SYSTEM CONSTANTS
    public List<LngLat> dailyFlightpath = new ArrayList<>();

    public DeliveryHandler(String baseURL, String date) {
        // BASE URL
        RestClient_J restClient = new RestClient_J();

        this.restaurants = restClient.getRestaurantsFromServer();
        this.orders = restClient.getOrdersFromServer(date);
        this.noFlyZones = restClient.getNoFlyZonesFromServer();
        this.centralArea = restClient.getCentralAreaFromServer();
    }

    public void deliverOrders() {
        OrderValidator orderValidator = new OrderValidator();

        for (Order order : this.orders) {
            // Validate order first
            orderValidator.validateOrder(order, this.restaurants);

            // Check when order is Valid_but_not delivered
            if (order.getOrderStatus() == (OrderStatus.VALID_BUT_NOT_DELIVERED)) {
                // Get order restaurant
                Restaurant orderRestaurant = orderValidator.findOrderRestaurant(order, this.restaurants);
                Cell orderRestaurantCell = new Cell(orderRestaurant.location().lng(), orderRestaurant.location().lat());

                List<LngLat> path = AStarPathFinder.findShortestPath(this.appletonTower, orderRestaurantCell, this.noFlyZones, this.centralArea);
                List<LngLat> pathBack = AStarPathFinder.findShortestPath(orderRestaurantCell, this.appletonTower, this.noFlyZones, this.centralArea);
                assert path != null;
                this.dailyFlightpath.addAll(path);
                assert pathBack != null;
                this.dailyFlightpath.addAll(pathBack);

                order.setOrderStatus(OrderStatus.DELIVERED);
            }
        }
    }
}
