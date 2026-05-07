package fr.univcotedazur.skimaster.customer.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Objects;

@Embeddable
public class Item {

    @Enumerated(EnumType.STRING)
    @NotNull
    private Plan plan;

    @Positive
    @NotNull
    private int quantity;

    public Item() {}

    public Item(Plan plan, int quantity) {
        this.plan = plan;
        this.quantity = quantity;
    }

    public Plan getPlan() {
        return plan;
    }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity){ this.quantity = quantity; }

    @Override
    public String toString() { return  plan.toString(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return Objects.equals(quantity, item.quantity) && plan == item.plan;
    }

    @Override
    public int hashCode() {
        return Objects.hash(plan, quantity);
    }
}