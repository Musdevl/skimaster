package fr.univ_cotedazur.gate.gate.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record AlertThresholdDTO(
        @PositiveOrZero(message = "warning_threshold should be zero or positive") int warning_threshold,
        @PositiveOrZero(message = "critical_threshold should be zero or positive") int critical_threshold) {
}