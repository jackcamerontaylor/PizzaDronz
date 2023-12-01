package uk.ac.ed.inf.communication;

import uk.ac.ed.inf.ilp.data.LngLat;

/**
 * Represents the format of a flight path in JSON, including order information and coordinates.
 */
public final class FlightPathJsonFormat {
    private final String orderNo;
    private final double fromLongitude;
    private final double fromLatitude;
    private final double angle;
    private final double toLongitude;
    private final  double toLatitude;

    /**
     * Constructs a FlightPathJsonFormat object for each movement.
     *
     * @param orderNo        The order number associated with the flight path movement.
     * @param fromPos        The starting position (longitude and latitude) of the move.
     * @param angle          The angle of the move (999 if hovering).
     * @param toPos          The ending position (longitude and latitude) of the move.
     */
    public FlightPathJsonFormat(String orderNo, LngLat fromPos, double angle, LngLat toPos) {
        this.orderNo = orderNo;
        this.fromLongitude = fromPos.lng();
        this.fromLatitude = fromPos.lat();
        this.angle = angle;
        this.toLongitude = toPos.lng();
        this.toLatitude = toPos.lat();
    }
}
