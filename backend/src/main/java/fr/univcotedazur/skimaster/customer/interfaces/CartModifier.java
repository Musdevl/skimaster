package fr.univcotedazur.skimaster.customer.interfaces;

import fr.univcotedazur.skimaster.customer.entities.Item;
import fr.univcotedazur.skimaster.customer.exceptions.CustomerIdNotFoundException;
import fr.univcotedazur.skimaster.customer.exceptions.NegativeQuantityException;

import java.util.Set;

public interface CartModifier {

    Item update(Long customerId, Item it) throws NegativeQuantityException, CustomerIdNotFoundException;

    Set<Item> cartContent(Long customerId) throws CustomerIdNotFoundException;

}