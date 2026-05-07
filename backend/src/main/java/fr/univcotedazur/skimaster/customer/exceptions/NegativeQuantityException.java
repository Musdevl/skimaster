package fr.univcotedazur.skimaster.customer.exceptions;

import fr.univcotedazur.skimaster.customer.entities.Plan;
public class NegativeQuantityException extends Exception {

    private String name;
    private Plan plan;
    private int potentialQuantity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public int getPotentialQuantity() {
        return potentialQuantity;
    }

    public void setPotentialQuantity(int potentialQuantity) {
        this.potentialQuantity = potentialQuantity;
    }

    public NegativeQuantityException() {}

    public NegativeQuantityException(String name, Plan plan, int potentialQuantity) {
        this.name = name;
        this.plan = plan;
        this.potentialQuantity = potentialQuantity;
    }
}