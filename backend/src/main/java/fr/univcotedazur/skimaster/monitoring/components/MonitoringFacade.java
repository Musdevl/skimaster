package fr.univcotedazur.skimaster.monitoring.components;

import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateStatusDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.PanelStatusDTO;
import fr.univcotedazur.skimaster.monitoring.dto.gate.ThresholdAlertDTO;
import fr.univcotedazur.skimaster.monitoring.entities.GateStatus;
import fr.univcotedazur.skimaster.monitoring.entities.PanelSeverity;
import fr.univcotedazur.skimaster.monitoring.dto.gate.ThresholdsDTO;
import fr.univcotedazur.skimaster.monitoring.interfaces.GateProcessor;
import fr.univcotedazur.skimaster.monitoring.interfaces.PanelProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MonitoringFacade {

    private final PanelProcessor panelHandler;
    private final GateProcessor gateHandler;

    public MonitoringFacade(GateHandler gateHandler,
            PanelHandler panelHandler) {
        this.panelHandler = panelHandler;
        this.gateHandler = gateHandler;
    }

    @Transactional
    public void configureThresholds(String gateId, ThresholdsDTO thresholds) {
        gateHandler.configureThresholds(gateId, thresholds);
    }

    @Transactional
    public void reportProblem(String gateId, String details) {
        gateHandler.getGateConnection(gateId);
        panelHandler.updateGateStatus(gateId, GateStatus.CLOSED,
                "Automatic closing of gate '" + gateId + "': " + safe(details));
    }

    @Transactional
    public void open(String gateId, String details) {
        gateHandler.getGateConnection(gateId);
        panelHandler.updateGateStatus(gateId, GateStatus.OPENED, "Opening of gate '" + gateId + "': " + safe(details));
    }

    @Transactional
    public void openManual(String gateId, String details) {
        gateHandler.openManual(gateId, details);
    }

    @Transactional
    public void close(String gateId, String details) {
        gateHandler.getGateConnection(gateId);
        panelHandler.updateGateStatus(gateId, GateStatus.CLOSED, "Closing of gate '" + gateId + "': " + safe(details));
    }

    @Transactional
    public void closeManual(String gateId, String details) {
        gateHandler.closeManual(gateId, details);
    }

    @Transactional
    public void handleThresholdAlert(ThresholdAlertDTO alert) {
        String message = String.format("Threshold alert received from gate '%s': gauge=%d, threshold=%d, severity=%s",
                alert.gateId(), alert.currentGauge(), alert.threshold(), alert.severity());
        panelHandler.write(message, alert.severity());
    }

    private static String safe(String s) {
        if (s == null || s.isBlank()) {
            return "(no details)";
        }
        return s;
    }

    @Transactional
    public void registerPanel(String panelId, String uri) {
        panelHandler.registerPanel(panelId, uri);
    }

    public String addPanelGateStatus(String panelId, String gateId) {
        GateStatusDTO gateStatusDTO = gateHandler.getGateStatus(gateHandler.getGateConnection(gateId));
        return panelHandler.addGateStatus(panelId, gateStatusDTO.gateId(), gateStatusDTO.status(),
                gateStatusDTO.detail());
    }

    public String removePanelGateStatus(String panelId, String gateId) {
        return this.panelHandler.removeGateStatus(panelId, gateId);
    }

    public PanelStatusDTO readPanel(String panelId) {
        return panelHandler.read(panelId);
    }

    public PanelStatusDTO writePanel(String panelId, String message, PanelSeverity severity) {
        return panelHandler.write(panelId, message, severity);
    }

}
