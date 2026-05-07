package fr.univcotedazur.skimaster.cli.dto;

import fr.univcotedazur.skimaster.cli.model.PanelSeverity;

public record CliPanelMessageDTO(String message, PanelSeverity severity) {
}
