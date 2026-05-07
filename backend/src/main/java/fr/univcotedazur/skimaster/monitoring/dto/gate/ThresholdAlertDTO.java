package fr.univcotedazur.skimaster.monitoring.dto.gate;

import fr.univcotedazur.skimaster.monitoring.entities.PanelSeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ThresholdAlertDTO(
                @NotBlank(message = "gateId should not be blank") String gateId,

                @PositiveOrZero(message = "currentGauge should be zero or positive") int currentGauge,

                @PositiveOrZero(message = "threshold should be zero or positive") int threshold,

                @NotNull(message = "severity should not be null") PanelSeverity severity) {
}