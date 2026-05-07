package fr.univcotedazur.skimaster.nfc.entities;

import fr.univcotedazur.skimaster.customer.entities.Customer;
import fr.univcotedazur.skimaster.customer.entities.Plan;
import fr.univcotedazur.skimaster.customer.entities.Sound;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class NFCCard {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @NotNull
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Sound sound;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Plan plan;

    protected NFCCard() {} // requis par JPA

    public NFCCard(Customer customer, Plan plan, Sound sound) {
        this.customer = customer;
        this.plan = plan;
        this.sound = sound;
        customer.addNfcCard(this);
    }

    public Long getCustomerId() { return this.customer.getId(); }
    public Customer getCustomer() { return this.customer; }
    public Plan getPlan() { return plan; }
    public Long getId() { return id; }
    public void setPlan(Plan plan) { this.plan = plan; }
    public Sound getSound(){ return this.sound; }
    public void setSound(Sound sound) { this.sound = sound; }
}