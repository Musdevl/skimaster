package fr.univcotedazur.skimaster.nfc.repositories;

import fr.univcotedazur.skimaster.customer.entities.Category;
import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.customer.entities.Plan;
import fr.univcotedazur.skimaster.customer.entities.Sound;
import fr.univcotedazur.skimaster.customer.repositories.CustomerRepository;
import fr.univcotedazur.skimaster.nfc.entities.NFCCard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class NFCCardRepositoryTest {

    @Autowired
    private NFCCardRepository nfcCardRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void getAllNFCCard() {
        Customer john = new Customer("john", "1234567890", Category.ADULT);
        customerRepository.saveAndFlush(john);
        NFCCard card = new NFCCard(john, Plan.BASIC_PLAN, Sound.HIGH_SOUND);
        nfcCardRepository.saveAndFlush(card);
        Assertions.assertEquals(nfcCardRepository.findAll().getFirst(),card);
    }

}