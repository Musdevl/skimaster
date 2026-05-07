package fr.univcotedazur.skimaster.customer.dto;

import fr.univcotedazur.skimaster.customer.entities.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

// Same DTO as input and output (no id in the input)
public record CustomerDTO(
                Long id, // expected to be empty when POSTing the creation of Customer, and containing
                         // the Id when returned
                @NotBlank(message = "name should not be blank") String name,
                @Pattern(regexp = "\\d{10}+", message = "credit card should be exactly 10 digits") String creditCard,
                @NotNull(message = "category should be ADULT or CHILD") Category category) {
}
