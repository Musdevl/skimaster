package fr.univ_cotedazur.gate.gate.controllers;

import fr.univ_cotedazur.gate.gate.components.GateService;
import fr.univ_cotedazur.gate.gate.components.NfcCardRegistry;
import fr.univ_cotedazur.gate.gate.connector.SkimasterProxy;
import fr.univ_cotedazur.gate.gate.dto.AlertThresholdDTO;
import fr.univ_cotedazur.gate.gate.dto.GateDailyReportDTO;
import fr.univ_cotedazur.gate.gate.dto.GateStatusDTO;
import fr.univ_cotedazur.gate.gate.entities.NfcCard;
import fr.univ_cotedazur.gate.gate.connector.externaldto.DetailDTO;
import fr.univ_cotedazur.gate.gate.dto.NFCCardDTO;
import fr.univ_cotedazur.gate.gate.entities.Plan;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GateController {

    private final NfcCardRegistry nfcCardRegistry;

    private final GateService gateService;

    private static final Logger LOG = LoggerFactory.getLogger(SkimasterProxy.class);

    public GateController(NfcCardRegistry nfcCardRegistry, GateService gateService) {
        this.nfcCardRegistry = nfcCardRegistry;
        this.gateService = gateService;
    }

    @GetMapping(path = "/status")
    public ResponseEntity<GateStatusDTO> status() {
        return ResponseEntity.ok(gateService.getStatus());
    }

    // On renvoie la nouvelle DB
    @PutMapping(path = "/refresh", consumes = "application/json")
    public ResponseEntity<List<NfcCard>> refresh(@RequestBody List<NFCCardDTO> newCards) {
        List<NfcCard> cards = newCards.stream()
                .map(dto -> new NfcCard(dto.id(), dto.customerId(), dto.sound(), dto.plan()))
                .toList();
        nfcCardRegistry.deleteAll();
        return ResponseEntity.ok(nfcCardRegistry.saveAll(cards));
    }

    @PostMapping(path = "/close")
    public ResponseEntity<String> closeGate(@RequestBody DetailDTO details) {
        return ResponseEntity.ok(this.gateService.closeGate(details.details()));
    }

    @PostMapping(path = "/open")
    public ResponseEntity<String> openGate(@RequestBody DetailDTO details) {
        return ResponseEntity.ok(this.gateService.openGate(details.details()));
    }

    @PutMapping(path = "/alert-thresholds", consumes = "application/json")
    public ResponseEntity<String> setAlertThreshold(@RequestBody @Valid AlertThresholdDTO alertThreshold) {
        return ResponseEntity.ok(this.gateService.setThresholds(alertThreshold));
    }

    @GetMapping(path = "/daily-super-card")
    public ResponseEntity<List<NFCCardDTO>> getDailySuperCard() {
        return ResponseEntity.ok(this.nfcCardRegistry.findTodaySuperCardScans());
    }

    @PostMapping(path = "/scan")
    public ResponseEntity<String> scanCard(@RequestBody Long nfcCardId) {
        return ResponseEntity.ok(this.gateService.checkCard(nfcCardId));
    }

    @PostMapping(path = "/report-issue")
    public ResponseEntity<String> reportIssue(@RequestBody DetailDTO details) {
        this.gateService.reportIssue(details.details());
        return ResponseEntity.ok("success");
    }

    @PostMapping(path = "/gates/register")
    public void registerGate(@RequestBody Long domainId) {
        this.gateService.registerGate(domainId);
    }

    @GetMapping(path = "/request-report")
    public ResponseEntity<GateDailyReportDTO> getDailyReport() {
        GateDailyReportDTO report = this.gateService.getDailyReport();
        return ResponseEntity.ok(report);
    }

    @PostMapping(path = "/add-card")
    public ResponseEntity<String> addCard(@RequestBody NFCCardDTO newCard) {
        NfcCard nfcCard = new NfcCard(newCard.id(), newCard.customerId(), newCard.sound(), newCard.plan());
        nfcCardRegistry.save(nfcCard);
        LOG.info("Card added: {}", nfcCard);
        return ResponseEntity.ok("success");
    }
}