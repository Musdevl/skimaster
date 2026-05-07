package fr.univcotedazur.skimaster.monitoring.dto;

import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateStatusDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.PanelStatusDTO;

import java.time.Instant;
import java.util.List;

public record DashboardDTO(Instant at,
                           List<GateStatusDTO> gates,
                           List<PanelStatusDTO> panels) {
}
