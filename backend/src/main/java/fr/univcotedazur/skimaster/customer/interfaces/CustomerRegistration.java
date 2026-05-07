package fr.univcotedazur.skimaster.customer.interfaces;

import fr.univcotedazur.skimaster.customer.entities.Category;
import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.customer.exceptions.AlreadyExistingCustomerException;

public interface CustomerRegistration {

    Customer register(String name, String creditCard, Category category)
            throws AlreadyExistingCustomerException;
}
