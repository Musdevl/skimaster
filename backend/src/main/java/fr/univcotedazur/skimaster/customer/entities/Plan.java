package fr.univcotedazur.skimaster.customer.entities;

public enum Plan {

    BASIC_PLAN("BASIC_PLAN", 10.0, true),
    BEGINNER_PASS("BEGINNER_PASS", 15.0, true),
    SUPER_CARD("SUPER_CARD", 15.0, true),
    FAMILY_PLAN("FAMILY_PLAN", 30.0, true),

    SUPER_CARD_PASSAGE("SUPER_CARD_PASSAGE", 1.0, false);

    private final String name;
    private final double price;
    private final boolean isSubscription;

    Plan(String name, double price, boolean isSubscription){
        this.name = name;
        this.price = price;
        this.isSubscription = isSubscription;
    }

    public boolean isSubscription() { return isSubscription; }

    public double getPrice(){
        return this.price;
    }

    public String getName(){ return this.name; }
}