package fr.univcotedazur.skimaster.nfc.interfaces;

import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.nfc.entities.NFCCard;
import fr.univcotedazur.skimaster.customer.entities.Plan;

import java.util.List;
import java.util.Optional;

public interface NFCCardFinder {

    List<NFCCard> findAllByPlan(Plan plan);

    Optional<NFCCard> findById(Long id);

    List<NFCCard> findAll();

    List<NFCCard> findAllByCustomer(Customer customer);

    Optional<NFCCard> findByCustomerAndPlan(Customer customer, Plan plan);

}