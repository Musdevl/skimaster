package fr.univcotedazur.skimaster.order.interfaces;

import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.order.entities.Order;


public interface OrderCreator {
    Order createOrder(Customer customer, double price, String payReceiptId);
}