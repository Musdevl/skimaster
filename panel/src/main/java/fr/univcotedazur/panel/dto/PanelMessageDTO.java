package fr.univcotedazur.panel.dto;

import fr.univcotedazur.panel.entities.PanelSeverity;

import java.time.Instant;

public record PanelMessageDTO(Instant at, PanelSeverity severity, String message) {
}
