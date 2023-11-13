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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Map to cache visited paths
    private Map<PathCacheKey, List<Cell>> visitedPathsCache = new HashMap<>();


    public DeliveryHandler(String baseURL, String date) {
        // BASE URL
        RestClient restClient = new RestClient(baseURL, date);

        this.restaurants = restClient.getRestaurantsFromServer();
        this.orders = restClient.getOrdersFromServer();
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

                List<Cell> path;
                List<Cell> pathBack;

                // Check if the path is already cached
                List<Cell> cachedPath = getVisitedPathFromCache(this.appletonTower, orderRestaurantCell);
                if (cachedPath == null) {
                    path = AStarPathFinder.findShortestPath(this.appletonTower, orderRestaurantCell, this.noFlyZones, this.centralArea);
                    addVisitedPathToCache(this.appletonTower, orderRestaurantCell, path);
                } else {
                    path = cachedPath;
                }

                // Check if the path is already cached
                List<Cell> cachedBackPath = getVisitedPathFromCache(orderRestaurantCell, this.appletonTower);
                if (cachedBackPath == null) {
                    pathBack = AStarPathFinder.findShortestPath(orderRestaurantCell, this.appletonTower, this.noFlyZones, this.centralArea);
                    addVisitedPathToCache(orderRestaurantCell, this.appletonTower, path);
                } else {
                    pathBack = cachedPath;
                }

                assert path != null;
                this.dailyFlightpath.addAll(path);
                assert pathBack != null;
                this.dailyFlightpath.addAll(pathBack);

                for (int i = 1; i < path.size(); i++) {
                    Cell cell = path.get(i);
                    FlightPathJsonFormat x = new FlightPathJsonFormat(order.getOrderNo(), cell.parentLngLat(), calculateAngle(cell), cell.currentLngLat());
                    this.dailyDroneMoves.add(x);
                }
                this.dailyDroneMoves.add(new FlightPathJsonFormat(order.getOrderNo(), orderRestaurantCell.currentLngLat(), 999, orderRestaurantCell.currentLngLat()));

                for (int i = 1; i < pathBack.size(); i++) {
                    Cell cell = pathBack.get(i);
                    FlightPathJsonFormat x = new FlightPathJsonFormat(order.getOrderNo(), cell.parentLngLat(), calculateAngle(cell), cell.currentLngLat());
                    this.dailyDroneMoves.add(x);
                }
                this.dailyDroneMoves.add(new FlightPathJsonFormat(order.getOrderNo(),this.appletonTower.currentLngLat(),999, this.appletonTower.currentLngLat()));

                order.setOrderStatus(OrderStatus.DELIVERED);
            }
        }
    }
    private double calculateAngle(Cell cell) {
        return (-(((cell.angle() - 90) + 360) % 360) + 360);
    }

    private static class PathCacheKey {
        private Cell start;
        private Cell end;

        public PathCacheKey(Cell start, Cell end) {
            this.start = start;
            this.end = end;
        }
    }

    private List<Cell> getVisitedPathFromCache(Cell start, Cell end) {
        PathCacheKey key = new PathCacheKey(start, end);
        return visitedPathsCache.get(key);
    }

    private void addVisitedPathToCache(Cell start, Cell end, List<Cell> path) {
        PathCacheKey key = new PathCacheKey(start, end);
        visitedPathsCache.put(key, path);
    }

    }
