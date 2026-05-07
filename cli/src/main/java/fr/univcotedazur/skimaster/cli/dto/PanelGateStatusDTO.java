package fr.univcotedazur.skimaster.cli.dto;

import fr.univcotedazur.skimaster.cli.model.GateStatus;

public record PanelGateStatusDTO(String gateName, GateStatus status, String detail) {
}
