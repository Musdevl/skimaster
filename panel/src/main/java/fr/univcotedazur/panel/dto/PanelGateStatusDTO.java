package fr.univcotedazur.panel.dto;

import fr.univcotedazur.panel.entities.GateStatus;

public record PanelGateStatusDTO(String gateName, GateStatus status, String detail){}
