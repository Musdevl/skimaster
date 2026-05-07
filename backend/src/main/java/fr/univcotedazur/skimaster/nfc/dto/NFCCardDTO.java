package fr.univcotedazur.skimaster.nfc.dto;

import fr.univcotedazur.skimaster.customer.entities.Plan;
import fr.univcotedazur.skimaster.customer.entities.Sound;
import jakarta.validation.constraints.NotNull;

public record NFCCardDTO(
                @NotNull Long id,
                @NotNull(message = "customerId should not be null") Long customerId,
                @NotNull(message = "sound should not be null") Sound sound,
                @NotNull(message = "plan should not be null") Plan plan) {
}
