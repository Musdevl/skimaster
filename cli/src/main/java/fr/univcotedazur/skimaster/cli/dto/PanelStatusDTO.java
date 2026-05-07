package fr.univcotedazur.skimaster.cli.dto;

import java.util.List;

public record PanelStatusDTO(List<PanelGateStatusDTO> gateStatusDTOList, PanelMessageDTO panelMessage) {}