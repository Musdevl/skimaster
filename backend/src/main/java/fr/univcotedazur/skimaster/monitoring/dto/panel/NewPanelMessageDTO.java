package fr.univcotedazur.skimaster.monitoring.dto.panel;

import fr.univcotedazur.skimaster.monitoring.entities.PanelSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewPanelMessageDTO(
        @NotBlank(message = "message should not be blank") String message,
        @NotNull(message = "severity should not be null") PanelSeverity severity) {
}
