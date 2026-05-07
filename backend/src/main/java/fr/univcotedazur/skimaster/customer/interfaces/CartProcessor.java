package fr.univcotedazur.skimaster.customer.interfaces;

import fr.univcotedazur.skimaster.cashier.exceptions.PaymentException;
import fr.univcotedazur.skimaster.customer.exceptions.CustomerIdNotFoundException;
import fr.univcotedazur.skimaster.customer.exceptions.EmptyCartException;
import fr.univcotedazur.skimaster.order.entities.Order;

public interface CartProcessor {

    double cartPrice(Long customerId) throws CustomerIdNotFoundException;

    Order validate(Long customerId) throws PaymentException, EmptyCartException, CustomerIdNotFoundException;

}