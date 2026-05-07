package fr.univcotedazur.skimaster.order.interfaces;

import fr.univcotedazur.skimaster.order.entities.Order;
import fr.univcotedazur.skimaster.order.entities.OrderStatus;
import fr.univcotedazur.skimaster.order.exceptions.OrderIdNotFoundException;

import java.util.List;
import java.util.Optional;

public interface OrderFinder {

    Optional<Order> findById(Long id);

    List<Order> findAll();

    Order retrieveOrder(Long orderId) throws OrderIdNotFoundException;

    OrderStatus retrieveOrderStatus(Long orderId) throws OrderIdNotFoundException;

}