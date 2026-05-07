package fr.univcotedazur.skimaster.monitoring.dto.panel;

import fr.univcotedazur.skimaster.monitoring.entities.PanelSeverity;

import java.time.Instant;

public record PanelMessageDTO(Instant at, PanelSeverity severity, String message) {
}