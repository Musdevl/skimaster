package fr.univcotedazur.skimaster.monitoring.controllers;

import fr.univcotedazur.skimaster.customer.entities.Plan;
import fr.univcotedazur.skimaster.monitoring.components.DomainHandler;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.AddPlanDomainDTO;
import fr.univcotedazur.skimaster.monitoring.dto.gate.GateRegisterDTO;
import fr.univcotedazur.skimaster.monitoring.entities.Domain;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/monitoring", produces = APPLICATION_JSON_VALUE)
public class DomainController {

    private final DomainHandler domainHandler;

    public DomainController(DomainHandler domainHandler) {
        this.domainHandler = domainHandler;
    }

    @PostMapping("/gates/register")
    public ResponseEntity<String> registerGate(@RequestBody @Valid GateRegisterDTO registerGateDTO) {
        domainHandler.registerGate(registerGateDTO.id(), registerGateDTO.URI(), registerGateDTO.domainId());
        return ResponseEntity.ok("Gate " + registerGateDTO.id() + " registered");
    }

    @GetMapping("/domains")
    public ResponseEntity<List<Domain>> listDomain() {
        return ResponseEntity.ok(domainHandler.listDomains());
    }

    @PostMapping("/domains")
    public ResponseEntity<Domain> createDomain(@RequestBody String domainName) {
        return ResponseEntity.ok(domainHandler.createDomain(domainName));
    }

    @PostMapping("/setup-gates-cards")
    public ResponseEntity<String> setupGatesCards() {
        try {
            domainHandler.setupDomainsCard();
            return ResponseEntity.ok("Gates Cards Setup Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add-domain-reserved-plan")
    public ResponseEntity<String> addPlanToDomain(@RequestBody AddPlanDomainDTO addPlanDomainDTO) {
        try {
            domainHandler.addPlanToDomain(Plan.valueOf(addPlanDomainDTO.plan()), addPlanDomainDTO.domainId());
            return ResponseEntity.ok("Plan successfully added to domain");
        } catch (Error e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
