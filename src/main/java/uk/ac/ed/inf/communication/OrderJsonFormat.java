package uk.ac.ed.inf.communication;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;

/**
 * Represents the format of a handled order in JSON, including order information, status and price.
 */
public final class OrderJsonFormat {
    private final String orderNo;
    private final OrderStatus orderStatus;
    private final OrderValidationCode orderValidationCode;
    private final int priceTotalInPence;

    /**
     * Constructs a FlightPathJsonFormat object with the specified parameters.
     *
     * @param orderNo                   The order number associated with the order.
     * @param orderStatus               The status given to that order.
     * @param orderValidationCode       The validation code (helps identify what was wrong if not delivered).
     * @param priceTotalInPence         The total price of the order including delivery.
     */
    public OrderJsonFormat(String orderNo, OrderStatus orderStatus, OrderValidationCode orderValidationCode, int priceTotalInPence) {
        this.orderNo = orderNo;
        this.orderStatus = orderStatus;
        this.orderValidationCode = orderValidationCode;
        this.priceTotalInPence = priceTotalInPence;
    }
}
