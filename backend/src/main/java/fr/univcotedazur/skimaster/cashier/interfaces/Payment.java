package fr.univcotedazur.skimaster.cashier.interfaces;

import fr.univcotedazur.skimaster.cashier.exceptions.PaymentException;
import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.order.entities.Order;

public interface Payment {

    Order payOrderFromCart(Customer customer, double price) throws PaymentException;

}
