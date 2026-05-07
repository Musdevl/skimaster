package fr.univcotedazur.skimaster.order.components;

import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.order.entities.Order;
import fr.univcotedazur.skimaster.order.entities.OrderStatus;
import fr.univcotedazur.skimaster.order.exceptions.OrderIdNotFoundException;
import fr.univcotedazur.skimaster.order.interfaces.OrderCreator;
import fr.univcotedazur.skimaster.order.interfaces.OrderFinder;
import fr.univcotedazur.skimaster.order.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class Orderer implements OrderCreator, OrderFinder {

    private final OrderRepository orderRepository;

    public Orderer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY) // must be called within a transaction
    public Order createOrder(Customer customer, double price, String payReceiptId) {
        return orderRepository.save(new Order(customer, customer.getCart(), price, payReceiptId));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Order retrieveOrder(Long orderId) throws OrderIdNotFoundException {
        return findById(orderId).orElseThrow(() -> new OrderIdNotFoundException(orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderStatus retrieveOrderStatus(Long orderId) throws OrderIdNotFoundException {
        return retrieveOrder(orderId).getStatus();
    }


}