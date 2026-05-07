package fr.univcotedazur.skimaster.cli.model;

public class CartElement {

    private PlanEnum plan;
    private int quantity;

    public PlanEnum getPlan() {
        return plan;
    }

    public void setPlan(PlanEnum plan) {
        this.plan = plan;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public CartElement() {
    }

    public CartElement(PlanEnum plan, int howMany) {
        this.plan = plan;
        this.quantity = howMany;
    }

    @Override
    public String toString() {
        return quantity + "x" + plan.toString();
    }

}
