package uk.ac.ed.inf.pathfinding;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

import java.util.Arrays;
import java.util.List;

/**
 * The LngLatHandler class implements LngLatHandling and provides methods for distance calculation,
 * checking proximity, checking if a point is inside a region, and determining the next position based on a given angle.
 */
public class LngLatHandler implements LngLatHandling {

    /**
     * Calculates the distance between two LngLat positions using the distance formula.
     *
     * @param startPosition the starting LngLat position.
     * @param endPosition   the ending LngLat position.
     * @return the distance between the two positions.
     */
    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        double term1 = Math.pow((startPosition.lng() - endPosition.lng()), 2);
        double term2 = Math.pow((startPosition.lat() - endPosition.lat()), 2);
        return Math.sqrt(term1 + term2);
    }

    /**
     * Checks if a LngLat position is close to another LngLat position based on a system constant.
     *
     * @param startPosition the starting LngLat position.
     * @param otherPosition  the other LngLat position to check proximity.
     * @return true if the positions are close, false otherwise.
     */
    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        return distanceTo(startPosition, otherPosition) < SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    /**
     * Checks if a LngLat position is inside a named region using ray casting.
     *
     * @param position the LngLat position to check.
     * @param region   the named region to check against.
     * @return true if the position is inside the region, false otherwise.
     */
    @Override
    public boolean isInRegion(LngLat position, NamedRegion region) {
        List<LngLat> vertices = Arrays.asList(region.vertices());
        double positionI = position.lng();
        double positionJ = position.lat();
        int vertexCount = vertices.size();
        int intersectCount = 0;

        // Using ray casting, we can loop through each pair of edges to see if there is an intersection point.
        for (int i = 0, j = vertexCount - 1; i < vertexCount; j = i ++) {
            LngLat vertexI = vertices.get(i);
            LngLat vertexJ = vertices.get(j);

            double longitudeI = vertexI.lng();
            double latitudeI = vertexI.lat();
            double longitudeJ = vertexJ.lng();
            double latitudeJ = vertexJ.lat();

            // Checks vertex point.
            if ((positionI == latitudeI) && (positionI == longitudeI) || (positionJ == latitudeJ) && (positionJ == longitudeJ)) {
                return true;
            }
            // Checks if it lies on the same latitude.
            else if ((positionJ == latitudeJ) && (positionJ == latitudeI) && ((positionI < longitudeI) != (positionI < longitudeJ))) {
                return true;
            }
            // Checks the general case.
            else if ((positionJ < latitudeI) != (positionJ < latitudeJ)) {
                double v = (longitudeJ - longitudeI) * (positionJ - latitudeI) / (latitudeJ - latitudeI) + longitudeI;
                if (positionI == v) {
                    return true;
                } else if (positionI <= v) {
                    intersectCount++;
                }
            }
        }
        // If the intersection count is odd, we can assume that it is inside the polygon.
        return intersectCount % 2 == 1;
    }

    /**
     * Determines the next LngLat position based on a given angle.
     *
     * @param startPosition the starting LngLat position.
     * @param angle          the angle of movement.
     * @return the next LngLat position.
     */
    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {
        // Will be stuck at the startPosition if it doesn't follow a legal angle.
        if (angle % 22.5 != 0) {
            return startPosition;
        }

        // We can use sin to determine how far up/down we have moved (latitude) and cos to determine how far left/right (longitude).
        // Thus, the change in latitude is the distance multiplied by each of these quantities for lat and lng respectively.
        double nextLat = startPosition.lat() + (SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(Math.toRadians(angle)));
        double nextLng = startPosition.lng() + (SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(Math.toRadians(angle)));

        return new LngLat(nextLng, nextLat);
    }
}
