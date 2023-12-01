package uk.ac.ed.inf.delivery;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;
import java.time.LocalDate;
import java.util.*;
import java.time.DayOfWeek;

/**
 * The OrderValidator class provides methods to validate orders, it implements the OrderValidation interface.
 */
public class OrderValidator implements OrderValidation {
    /**
     * Finds the restaurant from which the order originated based on the first pizza in the order.
     *
     * @param order              the order to be validated.
     * @param definedRestaurants an array of defined restaurants.
     * @return the restaurant from which the order originated.
     */
    public Restaurant findOrderRestaurant(Order order, Restaurant[] definedRestaurants) {
        // We are told pizzas are unique over restaurants.
        Pizza[] orderPizzas = order.getPizzasInOrder();
        Pizza firstPizza = orderPizzas[0];


        for (Restaurant restaurant : definedRestaurants) {
            if (Arrays.asList(restaurant.menu()).contains(firstPizza)) {
                return restaurant;
            }
        }
        return null;
    }

    private boolean validateCreditCardNumber (Order order) {
        String creditCardNumber = order.getCreditCardInformation().getCreditCardNumber();

        // Checking that the card number is 16 DIGITS long.
        return creditCardNumber.matches("\\d+") && creditCardNumber.length() == 16;
    }
    private boolean validateExpiryDate (Order order) {
        String expiryDate = order.getCreditCardInformation().getCreditCardExpiry();

        String[] parts = expiryDate.split("/");

        int month;
        int year;

        try {
            month = Integer.parseInt(parts[0]);
            year = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            // Catches parsing error quickly where the expiry date is not numbers.
            return false;
        }
        // Checking that the expiry date follows mm/yy and is valid today
        return expiryDate.matches("^(0[1-9]|1[0-2])/\\d{2}$") &&
                order.getOrderDate().isBefore(LocalDate.of(2000 + year, month, 1).plusMonths(1));
    }
    private boolean validateCvv (Order order) {
        String cvv = order.getCreditCardInformation().getCvv();

        // Checking that the cvv is 3 DIGITS long.
        return cvv.matches("\\d+") && cvv.length() == 3;
    }
    private boolean validateTotal (Order order, Restaurant[] definedRestaurants) {
        int orderCost = order.getPriceTotalInPence();

        // Start count at 100 because every order includes 100 pence per delivery.
        int totalCost = SystemConstants.ORDER_CHARGE_IN_PENCE;

        List<Pizza> definedPizzas = new ArrayList<>();

        // Gets a list of all real pizzas from all restaurants.
        for (Restaurant restaurant : definedRestaurants) {
            definedPizzas.addAll(Arrays.asList(restaurant.menu()));
        }

        // Nested loop to calculate the total cost according to the restaurant.
        for (Pizza orderPizza : order.getPizzasInOrder()) {
            String pizzaName = orderPizza.name();

            // Find the matching pizza in the restaurant's menu.
            for (Pizza restaurantPizza : definedPizzas) {
                if (pizzaName.equals(restaurantPizza.name())) {
                    totalCost += restaurantPizza.priceInPence();
                    break; // Break once the matching pizza is found.
                }
            }
        }
        return orderCost == totalCost;
    }
    private static int validatePizza (Order order, Restaurant[] definedRestaurants) {
        // Defines a list of valid pizzas, so we can check the ordered pizzas against them.
        List<Pizza> definedPizzas = new ArrayList<>();

        // Gets a list of all real pizzas from all restaurants.
        for (Restaurant restaurant : definedRestaurants) {
            definedPizzas.addAll(Arrays.asList(restaurant.menu()));
        }

        for (Pizza orderPizza : order.getPizzasInOrder()) {
            // 0 is no error,
            // 1 is pizza name incorrect,
            // 2 is pizza price incorrect.

            boolean pizzaFound = false;

            // Checking that the pizza exists and the price.
            for (Pizza definedPizza : definedPizzas) {
                if (orderPizza.name().equals(definedPizza.name())) {
                    pizzaFound = true;
                    // deals with case when pizza is valid but price is not.
                    if (orderPizza.priceInPence() != definedPizza.priceInPence()) {
                        return 2;
                    }
                }
            }
            if (!pizzaFound) {
                return 1;
            }
        }
        return 0;
    }
    private boolean validatePizzaCount (Order order) {
        // Returns true if number of pizzas are in a valid range.
        return order.getPizzasInOrder().length >= 1 && order.getPizzasInOrder().length <= SystemConstants.MAX_PIZZAS_PER_ORDER;
    }
    private boolean validateOneRestaurant (Order order, Restaurant[] definedRestaurants) {
        Restaurant helper = findOrderRestaurant(order, definedRestaurants);

        // Because pizzas are unique, we can assume if the current restaurant menu doesn't contain the ordered pizzas,
        // then the user has ordered from multiple restaurants.
        for (Pizza pizza : order.getPizzasInOrder()) {
            assert helper != null;
            if (!Arrays.stream(helper.menu()).toList().contains(pizza)) {
                return false;
            }
        }
        return true;
    }
    private boolean validateOpenRestaurant (Order order, Restaurant[] definedRestaurants) {
        Restaurant helper = findOrderRestaurant(order, definedRestaurants);

        // Create a HashSet for the days the restaurant is open and return true if the order was placed on a day that
        // the restaurant is open.
        assert helper != null;
        DayOfWeek[] openDays = helper.openingDays();
        Set<DayOfWeek> openDaysSet = new HashSet<>(Arrays.asList(openDays));
        DayOfWeek orderDay = order.getOrderDate().getDayOfWeek();

        return openDaysSet.contains(orderDay);
    }

    /**
     * Validates an order based on various criteria.
     *
     * @param orderToValidate    the order to be validated.
     * @param definedRestaurants an array of defined restaurants.
     * @return the validated order with updated validation code and order status.
     */
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        // Will check every validation code and if all are valid then the order is valid.

            if (!validatePizzaCount(orderToValidate)) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
            } else if (!validateCreditCardNumber(orderToValidate)) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
            } else if (!validateExpiryDate(orderToValidate)) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
            } else if (!validateCvv(orderToValidate)) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
            } else if (validatePizza(orderToValidate, definedRestaurants) == 1) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
            } else if (validatePizza(orderToValidate, definedRestaurants) == 2){
                orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
            } else if (!validateTotal(orderToValidate, definedRestaurants)) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
            } else if (!validateOneRestaurant(orderToValidate, definedRestaurants)) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
            } else if (!validateOpenRestaurant(orderToValidate, definedRestaurants)) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
            } else {
                orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
                orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
            }

        return orderToValidate;
    }
}
