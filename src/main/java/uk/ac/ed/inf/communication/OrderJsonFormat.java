package uk.ac.ed.inf.communication;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;

public final class OrderJsonFormat {
    private String orderNo;
    private OrderStatus orderStatus;
    private OrderValidationCode orderValidationCode;
    private int priceTotalInPence;

    public OrderJsonFormat(String orderNo, OrderStatus orderStatus, OrderValidationCode orderValidationCode, int priceTotalInPence) {
        this.orderNo = orderNo;
        this.orderStatus = orderStatus;
        this.orderValidationCode = orderValidationCode;
        this.priceTotalInPence = priceTotalInPence;
    }

}
