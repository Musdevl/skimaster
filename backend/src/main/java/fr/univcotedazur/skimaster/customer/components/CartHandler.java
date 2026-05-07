package fr.univcotedazur.skimaster.customer.components;

import fr.univcotedazur.skimaster.customer.exceptions.NegativeQuantityException;
import fr.univcotedazur.skimaster.cashier.exceptions.PaymentException;
import fr.univcotedazur.skimaster.cashier.interfaces.Payment;
import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.customer.entities.Item;
import fr.univcotedazur.skimaster.customer.exceptions.CustomerIdNotFoundException;
import fr.univcotedazur.skimaster.customer.exceptions.EmptyCartException;
import fr.univcotedazur.skimaster.customer.interfaces.CartModifier;
import fr.univcotedazur.skimaster.customer.interfaces.CartProcessor;
import fr.univcotedazur.skimaster.customer.interfaces.CustomerFinder;
import fr.univcotedazur.skimaster.order.entities.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
public class CartHandler implements CartModifier, CartProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(CartHandler.class);

    private final Payment payment;

    private final CustomerFinder customerFinder;

    public CartHandler(Payment payment, CustomerFinder customerFinder) {
        this.payment = payment;
        this.customerFinder = customerFinder;
    }

    @Override
    @Transactional
    public Item update(Long customerId, Item item) throws NegativeQuantityException, CustomerIdNotFoundException {
        Customer customer = customerFinder.retrieveCustomer(customerId);
        // some very basic logging (see the AOP way for a more powerful approach, in class ControllerLogger)
        LOG.info("SKIMASTER:Cart-Component: Updating cart of {} with {}", customer.getName(), item);
        int newQuantity = item.getQuantity();
        Set<Item> items = customer.getCart();
        Optional<Item> existing = items.stream().filter(e -> e.getPlan().equals(item.getPlan())).findFirst();
        if (existing.isPresent()) {
            newQuantity += existing.get().getQuantity();
        }
        if (newQuantity < 0) {
            throw new NegativeQuantityException(customer.getName(), item.getPlan(), newQuantity);
        } else {
            existing.ifPresent(items::remove);
            if (newQuantity > 0) {
                items.add(new Item(item.getPlan(), newQuantity));
            }
        }
        return new Item(item.getPlan(), newQuantity);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Item> cartContent(Long customerId) throws CustomerIdNotFoundException {
        return customerFinder.retrieveCustomer(customerId).getCart();
    }

    @Override
    @Transactional(readOnly = true)
    public double cartPrice(Long customerId) throws CustomerIdNotFoundException {
        return cartPriceFromCustomer(customerFinder.retrieveCustomer(customerId));
    }

    // private method will be transactional if called from a public transactional method
    private double cartPriceFromCustomer(Customer customer) {
        return customer.getCart().stream()
                .mapToDouble(item -> item.getPlan().getPrice() * item.getQuantity())
                .sum();
    }

    @Override
    @Transactional
    public Order validate(Long customerId) throws PaymentException, EmptyCartException, CustomerIdNotFoundException {
        Customer customer = customerFinder.retrieveCustomer(customerId);
        if (customer.getCart().isEmpty())
            throw new EmptyCartException(customer.getName());
        Order newOrder = payment.payOrderFromCart(customer, cartPriceFromCustomer(customer));
        customer.clearCart();
        return newOrder;
    }

}