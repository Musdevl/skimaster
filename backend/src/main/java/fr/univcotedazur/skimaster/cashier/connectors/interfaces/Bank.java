package fr.univcotedazur.skimaster.cashier.connectors.interfaces;

import fr.univcotedazur.skimaster.customer.entities.Customer;

import java.util.Optional;

public interface Bank {

    Optional<String> pay(Customer customer, double value);
}
