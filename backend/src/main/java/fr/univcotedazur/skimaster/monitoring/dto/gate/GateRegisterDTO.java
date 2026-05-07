package fr.univcotedazur.skimaster.monitoring.dto.gate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

public record GateRegisterDTO(
        @NotBlank(message = "URI should not be blank") @URL(message = "URI should be a valid URL") String URI,

        @NotBlank(message = "id should not be blank") String id,

        @NotNull(message = "domainId should not be null") Long domainId) {
}