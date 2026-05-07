package fr.univcotedazur.skimaster.monitoring.connectors.externaldto;


import fr.univcotedazur.skimaster.monitoring.dto.panel.PanelMessageDTO;

import java.util.List;

public record PanelStatusDTO(List<PanelGateStatusDTO> gateStatusDTOList, PanelMessageDTO panelMessage) {}
