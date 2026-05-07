package fr.univcotedazur.skimaster.monitoring.components;

import fr.univcotedazur.skimaster.customer.entities.Plan;
import fr.univcotedazur.skimaster.monitoring.connectors.GateProxy;
import fr.univcotedazur.skimaster.monitoring.entities.Domain;
import fr.univcotedazur.skimaster.monitoring.entities.GateConnection;
import fr.univcotedazur.skimaster.monitoring.repositories.DomainRepository;
import fr.univcotedazur.skimaster.nfc.components.NFCCardRegistry;
import fr.univcotedazur.skimaster.nfc.dto.NFCCardDTO;
import fr.univcotedazur.skimaster.nfc.entities.NFCCard;
import fr.univcotedazur.skimaster.nfc.repositories.NFCCardRepository;
import fr.univcotedazur.skimaster.customer.entities.Sound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DomainHandlerTest {

    @Mock DomainRepository domainRepository;
    @Mock NFCCardRepository nfcCardRepository;
    @Mock GateProxy gateProxy;
    @Mock NFCCardRegistry nfcCardRegistry;

    @InjectMocks DomainHandler domainHandler;

    private Domain domain;
    private GateConnection gate;
    private NFCCard nfcCard;
    private NFCCardDTO nfcCardDTO;

    @BeforeEach
    void setUp() {
        gate = mock(GateConnection.class);

        domain = mock(Domain.class);

        nfcCard = mock(NFCCard.class);
        nfcCardDTO = new NFCCardDTO((long) 1, (long) 1, Sound.LOW_SOUND, Plan.BASIC_PLAN);
    }

    // registerGate

    @Test
    void registerGate_domainNotFound_throwsRuntimeException() {
        when(domainRepository.findById((long) 99)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> domainHandler.registerGate("g1", "http://gate:8080", (long) 99));
    }

    @Test
    void registerGate_domainFound_addsGateConnection() {
        Domain realDomain = new Domain("Dom");
        when(domainRepository.findById((long) 1)).thenReturn(Optional.of(realDomain));

        domainHandler.registerGate("g1", "http://gate:8080", (long) 1);

        assertEquals(1, realDomain.getGateConnections().size());
    }

    // setupDomainsCard

    @Test
    void setupDomainsCard_noDomains_doesNothing() {
        when(domainRepository.findAll()).thenReturn(List.of());
        domainHandler.setupDomainsCard();
        verifyNoInteractions(nfcCardRepository, gateProxy);
    }

    @Test
    void setupDomainsCard_domainWithNoPlans_refreshesAllCards() {
        when(domain.getGateConnections()).thenReturn(Set.of(gate));
        when(gate.getURI()).thenReturn("http://gate:8080");
        when(domainRepository.findAll()).thenReturn(List.of(domain));
        when(domain.getPlans()).thenReturn(Set.of());
        when(nfcCardRepository.findAll()).thenReturn(List.of(nfcCard));
        when(nfcCardRegistry.convertNFCCardtoNFCCardDTO(nfcCard)).thenReturn(nfcCardDTO);

        domainHandler.setupDomainsCard();

        verify(gateProxy).refreshCards("http://gate:8080", List.of(nfcCardDTO));
    }

    @Test
    void setupDomainsCard_domainWithPlans_refreshesFilteredCards() {
        when(domain.getGateConnections()).thenReturn(Set.of(gate));
        when(gate.getURI()).thenReturn("http://gate:8080");
        when(domainRepository.findAll()).thenReturn(List.of(domain));
        when(domain.getPlans()).thenReturn(Set.of(Plan.BASIC_PLAN));
        when(nfcCardRepository.findAllByPlanIn(List.of(Plan.BASIC_PLAN))).thenReturn(List.of(nfcCard));
        when(nfcCardRegistry.convertNFCCardtoNFCCardDTO(nfcCard)).thenReturn(nfcCardDTO);

        domainHandler.setupDomainsCard();

        verify(gateProxy).refreshCards("http://gate:8080", List.of(nfcCardDTO));
        verify(nfcCardRepository, never()).findAll();
    }

    // addCardToGates

    @Test
    void addCardToGates_noDomains_doesNothing() {
        when(domainRepository.findAll()).thenReturn(List.of());
        domainHandler.addCardToGates(nfcCardDTO);
        verifyNoInteractions(gateProxy);
    }

    @Test
    void addCardToGates_domainWithNoPlans_cardAllowed_addsToAllGates() {
        when(domain.getGateConnections()).thenReturn(Set.of(gate));
        when(gate.getURI()).thenReturn("http://gate:8080");
        when(domainRepository.findAll()).thenReturn(List.of(domain));
        when(domain.getPlans()).thenReturn(Set.of());

        domainHandler.addCardToGates(nfcCardDTO);

        verify(gateProxy).addCard("http://gate:8080", nfcCardDTO);
    }

    @Test
    void addCardToGates_domainPlanMatchesCard_addsToGates() {
        when(domain.getGateConnections()).thenReturn(Set.of(gate));
        when(gate.getURI()).thenReturn("http://gate:8080");
        when(domainRepository.findAll()).thenReturn(List.of(domain));
        when(domain.getPlans()).thenReturn(Set.of(Plan.BASIC_PLAN));

        domainHandler.addCardToGates(nfcCardDTO);

        verify(gateProxy).addCard("http://gate:8080", nfcCardDTO);
    }

    @Test
    void addCardToGates_domainPlanDoesNotMatchCard_doesNotAddToGates() {
        when(domainRepository.findAll()).thenReturn(List.of(domain));
        when(domain.getPlans()).thenReturn(Set.of(Plan.SUPER_CARD));

        domainHandler.addCardToGates(nfcCardDTO);

        verifyNoInteractions(gateProxy);
    }

    // addPlanToDomain

    @Test
    void addPlanToDomain_domainNotFound_throwsRuntimeException() {
        when(domainRepository.findById((long) 99)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> domainHandler.addPlanToDomain(Plan.BASIC_PLAN, (long) 99));
    }

    @Test
    void addPlanToDomain_domainFound_addsPlanAndSaves() {
        Domain realDomain = new Domain("Dom");
        when(domainRepository.findById((long) 1)).thenReturn(Optional.of(realDomain));

        domainHandler.addPlanToDomain(Plan.BASIC_PLAN, (long) 1);

        assertTrue(realDomain.getPlans().contains(Plan.BASIC_PLAN));
        verify(domainRepository).save(realDomain);
    }

    // listDomains

    @Test
    void listDomains_returnsAllDomains() {
        when(domainRepository.findAll()).thenReturn(List.of(domain));
        assertEquals(List.of(domain), domainHandler.listDomains());
    }

    // createDomain

    @Test
    void createDomain_savesAndReturnsDomain() {
        Domain saved = new Domain("NewDom");
        when(domainRepository.save(any(Domain.class))).thenReturn(saved);

        Domain result = domainHandler.createDomain("NewDom");

        assertEquals("NewDom", result.getName());
        verify(domainRepository).save(any(Domain.class));
    }
}