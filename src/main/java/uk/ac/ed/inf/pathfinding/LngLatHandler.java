package uk.ac.ed.inf.pathfinding;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

import java.util.Arrays;
import java.util.List;

public class LngLatHandler implements LngLatHandling {
    @Override
    // Using the distance formula to compute the distance between two points.
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        double term1 = Math.pow((startPosition.lng() - endPosition.lng()), 2);
        double term2 = Math.pow((startPosition.lat() - endPosition.lat()), 2);
        return Math.sqrt(term1 + term2);
    }
    @Override
    // Defines when the drone is close using system constant.
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        return distanceTo(startPosition, otherPosition) < SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

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
