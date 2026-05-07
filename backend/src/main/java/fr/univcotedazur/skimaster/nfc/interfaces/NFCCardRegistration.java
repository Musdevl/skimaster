package fr.univcotedazur.skimaster.nfc.interfaces;

import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.customer.entities.Sound;
import fr.univcotedazur.skimaster.nfc.entities.NFCCard;
import fr.univcotedazur.skimaster.customer.entities.Plan;
import fr.univcotedazur.skimaster.nfc.exceptions.AlreadyExistingNFCCardException;

public interface NFCCardRegistration {

    NFCCard register(Customer customer, Plan plan, Sound sound)
            throws AlreadyExistingNFCCardException;
}