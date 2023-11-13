package uk.ac.ed.inf.communication;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.LngLat;

public class FlightPathJsonFormat {
    private String orderNo;
    private double fromLongitude;
    private double fromLatitude;
    private double angle;
    private double toLongitude;
    private double toLatitude;

    public FlightPathJsonFormat(String orderNo, LngLat fromPos, double angle, LngLat toPos) {
        this.orderNo = orderNo;
        this.fromLongitude = fromPos.lng();
        this.fromLatitude = fromPos.lat();
        this.angle = angle;
        this.toLongitude = toPos.lng();
        this.toLatitude = toPos.lat();
    }
}
