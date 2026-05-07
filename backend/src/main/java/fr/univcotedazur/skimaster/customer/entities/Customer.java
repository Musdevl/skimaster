package fr.univcotedazur.skimaster.customer.entities;


import fr.univcotedazur.skimaster.nfc.entities.NFCCard;
import fr.univcotedazur.skimaster.order.entities.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.*;

@Entity
public class Customer {

    @Id
    @GeneratedValue
    private Long id; // Whether Long/Int or UUID are better primary keys, exposable outside is a vast issue, keep it simple here

    @Column(unique = true)
    @NotBlank
    private String name;

    @OneToMany(cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY, mappedBy = "customer")
    private Set<Order> orders = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Item> cart = new HashSet<>();

    @Pattern(regexp = "\\d{10}+", message = "Invalid creditCardNumber")
    private String creditCard;

    @OneToMany(cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY, mappedBy = "customer")
    private Set<NFCCard> nfcCards = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @NotNull
    private Category category;

    public Customer() {}

    public Customer(String name, String creditCard, Category category) {
        this.name = name;
        this.creditCard = creditCard;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

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

    public Category getCategory() { return this.category; }

    public void setCategory(Category category) { this.category = category; }

    public void addOrder(Order o) {
        this.orders.add(o);
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public void setNfcCards(Set<NFCCard> cards) { this.nfcCards = cards; }

    public Set<NFCCard> getNfcCards() { return this.nfcCards; }

    public void addNfcCard(NFCCard card){ this.nfcCards.add(card); }

    public Set<Item> getCart() {
        return cart;
    }

    public void setCart(Set<Item> cart) { this.cart = cart; }

    public void clearCart() {
        this.cart.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(name, customer.name); //&& Objects.equals(creditCard, customer.creditCard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}