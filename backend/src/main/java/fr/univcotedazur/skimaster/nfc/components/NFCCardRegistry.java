package fr.univcotedazur.skimaster.nfc.components;

import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.customer.entities.Sound;
import fr.univcotedazur.skimaster.nfc.dto.NFCCardDTO;
import fr.univcotedazur.skimaster.nfc.entities.NFCCard;
import fr.univcotedazur.skimaster.customer.entities.Plan;
import fr.univcotedazur.skimaster.nfc.exceptions.AlreadyExistingNFCCardException;
import fr.univcotedazur.skimaster.nfc.interfaces.NFCCardFinder;
import fr.univcotedazur.skimaster.nfc.interfaces.NFCCardRegistration;
import fr.univcotedazur.skimaster.nfc.repositories.NFCCardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NFCCardRegistry implements NFCCardRegistration, NFCCardFinder {

    private final NFCCardRepository nfcCardRepository;

    public NFCCardRegistry(NFCCardRepository nfcCardRepository) {
        this.nfcCardRepository = nfcCardRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NFCCard> findAllByPlan(Plan plan){
        return this.nfcCardRepository.findAllByPlan(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NFCCard> findById(Long id) {
        return this.nfcCardRepository.findNFCCardById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NFCCard> findAll() {
        return this.nfcCardRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NFCCard> findAllByCustomer(Customer customer){
        return this.nfcCardRepository.findAllByCustomer(customer);
    }

    @Override
    @Transactional
    public NFCCard register(Customer customer, Plan plan, Sound sound) throws AlreadyExistingNFCCardException {
        NFCCard newNFCCard = new NFCCard(customer, plan, sound);
        return this.nfcCardRepository.save(newNFCCard);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NFCCard> findByCustomerAndPlan(Customer customer, Plan plan){
        return this.nfcCardRepository.findByCustomerAndPlan(customer, plan);
    }

    public NFCCardDTO convertNFCCardtoNFCCardDTO(NFCCard card){
        return new NFCCardDTO(card.getId(), card.getCustomerId(), card.getSound(), card.getPlan());
    }
}