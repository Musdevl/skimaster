package fr.univcotedazur.skimaster.monitoring.interfaces;

import fr.univcotedazur.skimaster.customer.entities.Plan;
import fr.univcotedazur.skimaster.monitoring.entities.Domain;
import fr.univcotedazur.skimaster.nfc.dto.NFCCardDTO;

import java.util.List;

public interface DomainManager {
    Domain createDomain(String domainName);
    List<Domain> listDomains();
    void addPlanToDomain(Plan plan, Long domainId);
    void setupDomainsCard();
    void registerGate(String gateId, String uri, Long domainId);
    void addCardToGates(NFCCardDTO newCard);
}
