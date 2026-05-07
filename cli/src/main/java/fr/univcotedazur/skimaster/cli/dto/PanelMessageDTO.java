package fr.univcotedazur.skimaster.cli.dto;

import fr.univcotedazur.skimaster.cli.model.PanelSeverity;import java.time.Instant;

public record PanelMessageDTO(Instant at, PanelSeverity severity, String message) {}
