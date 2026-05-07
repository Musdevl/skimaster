package fr.univcotedazur.skimaster.monitoring.dto.gate;

import jakarta.validation.constraints.PositiveOrZero;

public record ThresholdsDTO(
        @PositiveOrZero(message = "Warning should be zero or positive") int warning,
        @PositiveOrZero(message = "Critical should be zero or positive") int critical) {
}
