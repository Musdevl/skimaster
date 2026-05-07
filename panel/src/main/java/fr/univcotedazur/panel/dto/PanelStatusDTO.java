package fr.univcotedazur.panel.dto;

import java.util.List;

public record PanelStatusDTO(List<PanelGateStatusDTO> gateStatusDTOList, PanelMessageDTO panelMessage) {}
