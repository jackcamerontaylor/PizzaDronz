package uk.ac.ed.inf;

import junit.framework.TestCase;
import uk.ac.ed.inf.delivery.OrderValidator;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.*;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * This class contains JUnit test cases for the OrderValidator class, specifically focusing on various validation scenarios for orders.
 */
public class OrderValTests extends TestCase {
    private Restaurant[] definedRestaurants;
    private OrderValidator orderValidator;
    /**
     * Sets up the test environment by initializing necessary objects and data.
     *
     * @throws Exception if an error occurs during setup
     */
    protected void setUp() throws Exception {
        super.setUp();
        definedRestaurants = new Restaurant[] {
                new Restaurant("Sora Lella Vegan Restaurant", new LngLat(-3.202541470527649,55.943284737579376),
                        new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY},
                        new Pizza[] {new Pizza("Meat Lover", 1400), new Pizza("Vegan Delight", 1100)}),
                new Restaurant("Domino's Pizza - Edinburgh - Southside", new LngLat(-3.1838572025299072,55.94449876875712),
                        new DayOfWeek[] {DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                        new Pizza[] {new Pizza("Super Cheese", 1400), new Pizza("All Shrooms", 900)}),
        };
        orderValidator = new OrderValidator();
    }

    /**
     * Tests if the length of the credit card number is considered legal or not.
     */
    public void testCardLength() {
        Order order = new Order("111",
                LocalDate.of(2023, 11, 30),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                1400,
                new Pizza[] {new Pizza("Meat Lover", 1400)},
                null);
        order.setCreditCardInformation(new CreditCardInformation("111111111111111",
                "12/25",
                "123"));
        Order orderInvalidCardNumber = orderValidator.validateOrder(order, definedRestaurants);
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, orderInvalidCardNumber.getOrderValidationCode());
    }

    /**
     * Tests if the credit card number contains invalid characters and lengths.
     */
    public void testCardNumberType() {
        Order order = new Order("111",
                LocalDate.of(2023, 11, 30),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                1400,
                new Pizza[] {new Pizza("Meat Lover", 1400)},
                null);
        order.setCreditCardInformation(new CreditCardInformation("1111w11111111111",
                "12/25",
                "123"));
        Order orderInvalidCardNumber = orderValidator.validateOrder(order, definedRestaurants);
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, orderInvalidCardNumber.getOrderValidationCode());
    }

    /**
     * Tests if the credit card's expiry date is considered invalid.
     */
    public void testExpiryDate() {
        Order order = new Order("111",
                LocalDate.of(2023, 11, 30),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                1400,
                new Pizza[] {new Pizza("Meat Lover", 1400)},
                null);
        order.setCreditCardInformation(new CreditCardInformation("1111w11111111111",
                "12/25",
                "123"));
        Order orderInvalidCardNumber = orderValidator.validateOrder(order, definedRestaurants);
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, orderInvalidCardNumber.getOrderValidationCode());
    }

    /**
     * Tests if the credit card's CVV is considered invalid.
     */
    public void testCVV() {
        Order order = new Order("111",
                LocalDate.of(2023, 11, 30),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                1400,
                new Pizza[] {new Pizza("Meat Lover", 1400)},
                null);
        order.setCreditCardInformation(new CreditCardInformation("1111111111111111",
                "12/25",
                "12x"));
        Order orderInvalidCardNumber = orderValidator.validateOrder(order, definedRestaurants);
        assertEquals(OrderValidationCode.CVV_INVALID, orderInvalidCardNumber.getOrderValidationCode());
    }

    /**
     * Tests if the order's total amount is considered incorrect.
     */
    public void testTotal() {
        Order order = new Order("111",
                LocalDate.of(2023, 11, 30),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                1400,
                new Pizza[] {new Pizza("Meat Lover", 14000)},
                null);
        order.setCreditCardInformation(new CreditCardInformation("1111111111111111",
                "12/25",
                "123"));
        Order orderInvalidCardNumber = orderValidator.validateOrder(order, definedRestaurants);
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, orderInvalidCardNumber.getOrderValidationCode());
    }

    /**
     * Tests if the name of the pizza in the order is considered invalid.
     */
    public void testPizzaName() {
        Order order = new Order("111",
                LocalDate.of(2023, 11, 30),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                1400,
                new Pizza[] {new Pizza("MeatLover", 1400)},
                null);
        order.setCreditCardInformation(new CreditCardInformation("1111111111111111",
                "12/25",
                "123"));
        Order orderInvalidCardNumber = orderValidator.validateOrder(order, definedRestaurants);
        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, orderInvalidCardNumber.getOrderValidationCode());
    }

    /**
     * Tests if the order contains pizzas from different restaurants.
     */
    public void testMultipleRestaurant() {
        Order order = new Order("111",
                LocalDate.of(2023, 11, 30),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                2900,
                new Pizza[] {new Pizza("Meat Lover", 1400),
                        new Pizza("Super Cheese", 1400)},
                null);
        order.setCreditCardInformation(new CreditCardInformation("1111111111111111",
                "12/25",
                "123"));
        Order orderInvalidCardNumber = orderValidator.validateOrder(order, definedRestaurants);
        assertEquals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, orderInvalidCardNumber.getOrderValidationCode());
    }

    /**
     * Tests if the restaurant is closed on the specified order date.
     */
    public void testRestaurantClosed(){
        Order order = new Order("111",
                LocalDate.of(2023, 11, 28),
                OrderStatus.UNDEFINED,
                OrderValidationCode.UNDEFINED,
                1500,
                new Pizza[] {new Pizza("Super Cheese", 1400)},
                null);

        order.setCreditCardInformation(new CreditCardInformation("1234567890123456",
                "12/25",
                "123"));

        Order orderInvalidType = orderValidator.validateOrder(order, definedRestaurants);
        assertEquals(OrderValidationCode.RESTAURANT_CLOSED, orderInvalidType.getOrderValidationCode());
    }
}
