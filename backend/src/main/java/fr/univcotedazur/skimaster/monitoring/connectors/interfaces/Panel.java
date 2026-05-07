package fr.univcotedazur.skimaster.monitoring.connectors.interfaces;

import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.PanelGateStatusDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.PanelStatusDTO;
import fr.univcotedazur.skimaster.monitoring.dto.panel.PanelMessageDTO;
import fr.univcotedazur.skimaster.monitoring.entities.GateStatus;

import java.util.Optional;

public interface Panel {

    Optional<PanelStatusDTO> read(String panelUri);

    Optional<PanelStatusDTO> write(String panelUri, PanelMessageDTO messageDTO);

    Optional<PanelGateStatusDTO> updateGateStatus(String panelUri, String gateName, GateStatus status, String detail);

    Optional<String> addGateStatus(String panelUri, String gateName, GateStatus status, String detail);

    Optional<String> removeGateStatus(String panelUri, String gateName);

}
