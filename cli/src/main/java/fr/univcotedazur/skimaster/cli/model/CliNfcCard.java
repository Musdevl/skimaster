package fr.univcotedazur.skimaster.cli.model;

public record CliNfcCard(
        Long id,
        Long customerId,
        PlanEnum plan,
        Sound sound) {
}
