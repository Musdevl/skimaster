package fr.univcotedazur.skimaster.monitoring.dto.panel;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;

public record PanelRegisterDTO(
        @NotBlank(message = "panelId should not be blank") String panelId,
        @NotBlank(message = "URI should not be blank") @URL(message = "URI should be a valid URL") String URI) {
}
