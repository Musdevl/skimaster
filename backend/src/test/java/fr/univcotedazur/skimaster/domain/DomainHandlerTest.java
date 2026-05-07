package fr.univcotedazur.skimaster.domain;

import fr.univcotedazur.skimaster.customer.entities.Category;
import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.customer.entities.Plan;
import fr.univcotedazur.skimaster.customer.entities.Sound;
import fr.univcotedazur.skimaster.customer.repositories.CustomerRepository;
import fr.univcotedazur.skimaster.monitoring.components.DomainHandler;
import fr.univcotedazur.skimaster.monitoring.connectors.GateProxy;
import fr.univcotedazur.skimaster.monitoring.entities.Domain;
import fr.univcotedazur.skimaster.monitoring.entities.GateConnection;
import fr.univcotedazur.skimaster.monitoring.repositories.DomainRepository;
import fr.univcotedazur.skimaster.nfc.entities.NFCCard;
import fr.univcotedazur.skimaster.nfc.repositories.NFCCardRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;


import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class DomainHandlerTest {

    @Autowired private DomainHandler domainHandler;
    @Autowired private DomainRepository domainRepository;
    @Autowired private NFCCardRepository nfcCardRepository;
    @Autowired private CustomerRepository customerRepository;

    @MockitoBean private GateProxy gateProxy; // mock uniquement le proxy externe

    private Customer customer;
    private Domain domain;

    @BeforeEach
    void setUpContext() {
        customer = new Customer("dorian", "1234896983", Category.ADULT);
        customerRepository.save(customer);

        domain = new Domain("domaine1");
        domain.addGateConnection(new GateConnection("gate1", "http://gate1.uri"));
        domainRepository.save(domain);
    }

    @AfterEach
    void cleanUpContext() {
        nfcCardRepository.deleteAll();
        domainRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void setupDomainsCard_noPlanConstraint_sendsAllCards() {
        // Aucun plan sur le domain = toutes les cartes doivent être envoyées
        nfcCardRepository.save(new NFCCard(customer, Plan.BASIC_PLAN, Sound.HIGH_SOUND));
        nfcCardRepository.save(new NFCCard(customer, Plan.SUPER_CARD, Sound.HIGH_SOUND));

        domainHandler.setupDomainsCard();

        verify(gateProxy).refreshCards(
                eq("http://gate1.uri"),
                argThat(cards -> cards.size() == 2)
        );
    }

    @Test
    void setupDomainsCard_withPlanConstraint_sendsOnlyMatchingCards() {
        // Domain restreint à BASIC_PLAN
        domain.addPlan(Plan.BASIC_PLAN);

        nfcCardRepository.save(new NFCCard(customer, Plan.BASIC_PLAN, Sound.HIGH_SOUND));
        nfcCardRepository.save(new NFCCard(customer, Plan.SUPER_CARD, Sound.HIGH_SOUND));

        domainHandler.setupDomainsCard();

        verify(gateProxy).refreshCards(
                eq("http://gate1.uri"),
                argThat(cards -> cards.size() == 1)
        );
    }

    @Test
    void setupDomainsCard_multipleGates_sendsToAllGates() {
        domain.addGateConnection(new GateConnection("gate2", "http://gate2.uri"));
        nfcCardRepository.save(new NFCCard(customer, Plan.BASIC_PLAN, Sound.HIGH_SOUND));

        domainHandler.setupDomainsCard();

        verify(gateProxy).refreshCards(eq("http://gate1.uri"), argThat(cards -> cards.size() == 1));
        verify(gateProxy).refreshCards(eq("http://gate2.uri"), argThat(cards -> cards.size() == 1));
    }

}