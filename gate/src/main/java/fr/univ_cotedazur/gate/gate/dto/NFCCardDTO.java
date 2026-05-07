package fr.univ_cotedazur.gate.gate.dto;

import fr.univ_cotedazur.gate.gate.entities.Sound;
import fr.univ_cotedazur.gate.gate.entities.Plan;
import jakarta.validation.constraints.NotNull;

public record NFCCardDTO
        (@NotNull Long id,
         @NotNull Long customerId,
         @NotNull Sound sound,
         @NotNull Plan plan) {
}
