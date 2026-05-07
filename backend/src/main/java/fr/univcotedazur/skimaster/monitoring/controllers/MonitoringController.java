package fr.univcotedazur.skimaster.monitoring.controllers;

import fr.univcotedazur.skimaster.monitoring.components.MonitoringFacade;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.DetailDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.PanelStatusDTO;
import fr.univcotedazur.skimaster.monitoring.dto.gate.GateInformationDTO;
import fr.univcotedazur.skimaster.monitoring.dto.gate.ThresholdAlertDTO;
import fr.univcotedazur.skimaster.monitoring.dto.gate.ThresholdsDTO;
import fr.univcotedazur.skimaster.monitoring.dto.panel.NewPanelMessageDTO;
import fr.univcotedazur.skimaster.monitoring.dto.panel.PanelRegisterDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/monitoring", produces = APPLICATION_JSON_VALUE)
public class MonitoringController {

    private final MonitoringFacade monitoringFacade;

    public MonitoringController(MonitoringFacade monitoringFacade) {
        this.monitoringFacade = monitoringFacade;
    }

    @PostMapping("/panels/register")
    public ResponseEntity<String> registerPanel(@RequestBody @Valid PanelRegisterDTO registerPanelDTO) {
        monitoringFacade.registerPanel(registerPanelDTO.panelId(), registerPanelDTO.URI());
        return ResponseEntity.ok("Panel " + registerPanelDTO.panelId() + " registered");
    }

    @PostMapping(path = "/panels/{panelId}")
    public ResponseEntity<String> addGateStatus(@PathVariable String panelId, @RequestBody String gateId) {
        return ResponseEntity.ok(monitoringFacade.addPanelGateStatus(panelId, gateId));
    }

    @GetMapping("/panels/{panelId}")
    public ResponseEntity<PanelStatusDTO> read(@PathVariable String panelId) {
        return ResponseEntity.ok(monitoringFacade.readPanel(panelId));
    }

    @PutMapping("/panels/{panelId}")
    public ResponseEntity<PanelStatusDTO> write(@PathVariable String panelId,
                                                @RequestBody @Valid NewPanelMessageDTO newPanelMessageDTO) {
        return ResponseEntity
                .ok(monitoringFacade.writePanel(panelId, newPanelMessageDTO.message(), newPanelMessageDTO.severity()));
    }

    @DeleteMapping("/panels/{panelName}/gate/{gateName}")
    public ResponseEntity<String> deleteGateStatus(@PathVariable String panelName, @PathVariable String gateName) {
        return ResponseEntity.ok(monitoringFacade.removePanelGateStatus(panelName, gateName));
    }








    @PutMapping(path = "/gates/{gateId}/thresholds", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> setThresholds(@PathVariable String gateId, @RequestBody @Valid ThresholdsDTO thresholds) {
        monitoringFacade.configureThresholds(gateId, thresholds);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/gates/{gateId}/issues", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> reportProblem(@PathVariable String gateId, @RequestBody DetailDTO body) {
        monitoringFacade.reportProblem(gateId, body != null ? body.details() : "");
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/gates/{gateId}/open", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> open(@PathVariable String gateId, @RequestBody DetailDTO body) {
        monitoringFacade.openManual(gateId, body != null ? body.details() : "");
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/gates/{gateId}/close", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> close(@PathVariable String gateId, @RequestBody DetailDTO body) {
        monitoringFacade.closeManual(gateId, body != null ? body.details() : "");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/gates/inform-gate-opening")
    public ResponseEntity<String> informGateOpening(@RequestBody @Valid GateInformationDTO openingGateDTO) {
        monitoringFacade.open(openingGateDTO.gateName(), openingGateDTO.details());
        return ResponseEntity.ok("Gate Opened Successfully");
    }

    @PostMapping("/gates/inform-gate-closing")
    public ResponseEntity<String> informGateClosing(@RequestBody @Valid GateInformationDTO closingGateDTO) {
        monitoringFacade.close(closingGateDTO.gateName(), closingGateDTO.details());
        return ResponseEntity.ok("Gate Closed Successfully");
    }

    @PostMapping(path = "/threshold-alert", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> thresholdAlert(@RequestBody @Valid ThresholdAlertDTO alert) {
        monitoringFacade.handleThresholdAlert(alert);
        return ResponseEntity.ok().build();
    }

}
