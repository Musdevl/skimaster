package fr.univcotedazur.skimaster.monitoring.dto.nfc;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record SuperCardInvoiceDTO(
                @NotNull(message = "nfcCardId should not be null") Long nfcCardId,

                @NotBlank(message = "plan should not be blank") String plan,

                @Positive(message = "unitPrice should be strictly positive") double unitPrice,

                @PositiveOrZero(message = "quantity should be zero or positive") int quantity,

                @PositiveOrZero(message = "totalPrice should be zero or positive") double totalPrice) {
}