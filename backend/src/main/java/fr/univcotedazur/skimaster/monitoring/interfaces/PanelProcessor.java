package fr.univcotedazur.skimaster.monitoring.interfaces;

import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.PanelStatusDTO;
import fr.univcotedazur.skimaster.monitoring.entities.GateStatus;
import fr.univcotedazur.skimaster.monitoring.entities.PanelSeverity;

public interface PanelProcessor {

    PanelStatusDTO read(String panelId);

    PanelStatusDTO write(String panelId, String message, PanelSeverity severity);

    void write(String message, PanelSeverity severity);

    void updateGateStatus(String gateName, GateStatus status, String detail);

    String addGateStatus(String panelId, String gateName, GateStatus status, String detail);

    String removeGateStatus(String panelId, String gateName);

    void registerPanel(String panelId, String uri);
}
