package fr.univcotedazur.skimaster.cli.model;

public enum PlanEnum {

    // On the client side, we only know cookies by their names...
    // We could have built a different representation from the JSON array
    // But this is a MVP...
    // Note that the enum name is different!
    BASIC_PLAN,
    SUPER_CARD,
    BEGINNER_PASS,
    FAMILY_PLAN
}
