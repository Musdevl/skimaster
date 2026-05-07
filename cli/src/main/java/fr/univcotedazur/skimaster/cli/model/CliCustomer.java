package fr.univcotedazur.skimaster.cli.model;

// A cli side class being equivalent to the backend CustomerDTO, in terms of attributes
// so that the automatic JSON (de-)/serialization will make the two compatible on each side
public class CliCustomer {

    private Long id;
    private String name;
    private String creditCard;
    private Category category;

    public CliCustomer(String name, String creditCard, Category category) {
        this.name = name;
        this.creditCard = creditCard;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() { return this.category; }

    public void setCategory(Category category) { this.category = category; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", creditCard='" + creditCard + '\'' +
                '}';
    }
}
