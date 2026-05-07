package fr.univcotedazur.skimaster.monitoring.components;

import fr.univcotedazur.skimaster.customer.entities.Plan;
import fr.univcotedazur.skimaster.monitoring.connectors.GateProxy;
import fr.univcotedazur.skimaster.monitoring.entities.Domain;
import fr.univcotedazur.skimaster.monitoring.entities.GateConnection;
import fr.univcotedazur.skimaster.monitoring.interfaces.DomainManager;
import fr.univcotedazur.skimaster.monitoring.repositories.DomainRepository;
import fr.univcotedazur.skimaster.nfc.components.NFCCardRegistry;
import fr.univcotedazur.skimaster.nfc.dto.NFCCardDTO;
import fr.univcotedazur.skimaster.nfc.entities.NFCCard;
import fr.univcotedazur.skimaster.nfc.repositories.NFCCardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class DomainHandler implements DomainManager {

    private final DomainRepository domainRepository;
    private final NFCCardRepository nfcCardRepository;
    private final GateProxy gateProxy;
    private final NFCCardRegistry nfcCardRegistry;

    public DomainHandler(DomainRepository domainRepository, NFCCardRepository nfcCardRepository, GateProxy gateProxy, NFCCardRegistry nfcCardRegistry) {
        this.domainRepository = domainRepository;
        this.nfcCardRepository = nfcCardRepository;
        this.gateProxy = gateProxy;
        this.nfcCardRegistry = nfcCardRegistry;
    }

    @Override
    public void registerGate(String gateId, String uri, Long domainId) {
        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new RuntimeException("Domain not found: " + domainId));

        GateConnection newCon = new GateConnection(gateId, uri);
        newCon.setDomain(domain);

        domain.addGateConnection(newCon);
    }

    @Override
    public void setupDomainsCard() {
        listDomains().forEach(domain -> {
            Set<Plan> plans = domain.getPlans();
            List<Plan> planList = plans.stream().toList();

            List<NFCCard> validCards = plans.isEmpty() ? nfcCardRepository.findAll() : nfcCardRepository.findAllByPlanIn(planList);

            List<NFCCardDTO> validCardDTOs = validCards.stream()
                    .map(nfcCardRegistry::convertNFCCardtoNFCCardDTO)
                    .toList();

            domain.getGateConnections().forEach(gateConnection -> {
                        this.gateProxy.refreshCards(gateConnection.getURI(), validCardDTOs);
                    }
            );
        });
    }

    @Override
    public void addCardToGates(NFCCardDTO newCard) {
        listDomains().forEach(domain -> {
            Set<Plan> plans = domain.getPlans();

            boolean isCardAllowed = plans.isEmpty() || plans.contains(newCard.plan());

            if (isCardAllowed) {
                domain.getGateConnections().forEach(gateConnection ->
                        gateProxy.addCard(gateConnection.getURI(), newCard)
                );
            }
        });
    }

    @Override
    public void addPlanToDomain(Plan plan, Long domainId) {
        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new RuntimeException("Domain not found: " + domainId));
        domain.addPlan(plan);
        domainRepository.save(domain);
    }

    @Override
    public List<Domain> listDomains(){
        return domainRepository.findAll();
    }

    @Override
    public Domain createDomain(String domainName) {
        return this.domainRepository.save(new Domain(domainName));
    }
}
