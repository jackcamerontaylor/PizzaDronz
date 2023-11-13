package uk.ac.ed.inf.pathfinding;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.constant.SystemConstants;

import java.util.*;

public class AStarPathFinder {
    static LngLatHandler lngLatHandler = new LngLatHandler();

    // Possible movement directions
    private static final double[] ANGLES = {
            0.0, 22.5, 45.0, 67.5, 90.0, 112.5, 135.0, 157.5,
            180.0, 202.5, 225.0, 247.5, 270.0, 292.5, 315.0, 337.5
    };

    // global defined variables for the search
    static PriorityQueue<Cell> openSet = new PriorityQueue<>();   // frontier
    static HashSet<Cell> closedSet = new HashSet<>();
    static List<Cell> path = new ArrayList<>();

    // A* search algorithm
    public static List<Cell> findShortestPath(Cell start, Cell goal, NamedRegion[] noFlyZones, NamedRegion centralArea) {

        // Reset the data structures for each new search
        openSet = new PriorityQueue<>();
        closedSet = new HashSet<>();

        // flag to check if drone starts outside the central area
        boolean startOutsideCentralArea = false;

        if (!lngLatHandler.isInRegion(start.cellLngLat, centralArea)) {
            startOutsideCentralArea = true;
        }

        // add start to the queue first
        openSet.add(start);

        // once there is element in the queue, then keep running
        while (!openSet.isEmpty()) {

            // Retrieves and removes the element with the highest priority
            Cell current = openSet.poll();

            // mark the cell to be visited
            closedSet.add(current);

            // find the goal: early exit
            if (lngLatHandler.isCloseTo(current.cellLngLat, goal.cellLngLat)) {

                // Reconstruct the path: trace by find the parent cell
                path = new ArrayList<>();
                while (current != null) {
                    path.add(current);
                    current = current.parent;
                }
                Collections.reverse(path);

                return path;
            }

            boolean nowInCentral = false;
            if (startOutsideCentralArea && lngLatHandler.isInRegion(current.cellLngLat, centralArea)) {
                nowInCentral = true;
            }

            // search neighbors
            for (double angle : ANGLES) {
                // neighbour cell location as a LngLat object
                LngLat newPos = lngLatHandler.nextPosition(current.cellLngLat, angle);

                // check if point is in noFlyZone and we haven't already looked at the point
                if (!isPointInNoFlyZone(newPos, noFlyZones) && !closedSet.contains(new Cell(newPos.lng(), newPos.lat()))) {

                    if (nowInCentral && !lngLatHandler.isInRegion(newPos, centralArea)){
                        continue;
                    }
                    // new movement is always 1 cost even it is diagonal
                    double tentativeG = current.g + SystemConstants.DRONE_MOVE_DISTANCE;

                    // find the cell if it is in the frontier but not visited to see if cost updating is needed
                    Cell existing_neighbor = findNeighbor(newPos.lng(), newPos.lat());

                    if(existing_neighbor != null){
                        // Check if this path is better than any previously generated path to the neighbor
                        if(tentativeG < existing_neighbor.g){
                            // update cost, parent information
                            existing_neighbor.parent = current;
                            existing_neighbor.g = tentativeG;
                            existing_neighbor.h = heuristic(existing_neighbor, goal);
                            existing_neighbor.f = existing_neighbor.g + existing_neighbor.h;
                            existing_neighbor.fromAngle = angle;
                        }
                    }
                    else{
                        // or directly add this cell to the frontier
                        Cell neighbor = new Cell(newPos.lng(), newPos.lat());
                        neighbor.parent = current;
                        neighbor.g = tentativeG;
                        neighbor.h = heuristic(neighbor, goal);
                        neighbor.f = neighbor.g + neighbor.h;
                        neighbor.fromAngle = angle;

                        openSet.add(neighbor);
                    }
                }
            }
        }

        // No path found
        return null;
    }

    // Helper function to find and return the neighbor cell
    //  Java priority queue cannot return a specific element
    public static Cell findNeighbor(double lat, double lng){
        if(openSet.isEmpty()){
            return null;
        }

        Iterator<Cell> iterator = openSet.iterator();

        Cell find = null;
        while (iterator.hasNext()) {
            Cell next = iterator.next();
            if(next.lat == lat && next.lng == lng){
                find = next;
                break;
            }
        }
        return find;
    }

    // Helper function to check if a cell is part of the path
//    public static boolean isInPath(int row, int col) {
//        for (Cell cell : path) {
//            if (cell.row == row && cell.col == col) {
//                return true;
//            }
//        }
//        return false;
//    }

    public static double heuristic(Cell a, Cell b) {
        // A simple heuristic: Manhattan distance
        return Math.abs(a.lng - b.lng) + Math.abs(a.lat - b.lat);
    }

    public static boolean isPointInNoFlyZone(LngLat point, NamedRegion[] noFlyZones) {
        for (NamedRegion noFlyZone : noFlyZones) {
            if (lngLatHandler.isInRegion(point, noFlyZone)) {
                return true;
            }
        }
        return false;
    }

}