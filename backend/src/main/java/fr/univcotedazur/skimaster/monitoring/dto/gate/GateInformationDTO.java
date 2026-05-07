package fr.univcotedazur.skimaster.monitoring.dto.gate;

import jakarta.validation.constraints.NotBlank;

public record GateInformationDTO(
        @NotBlank(message = "name should not be blank") String gateName,
        String details) {
}
