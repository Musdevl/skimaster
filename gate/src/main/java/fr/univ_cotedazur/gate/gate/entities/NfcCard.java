package fr.univ_cotedazur.gate.gate.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;


@Entity
@Table(name="nfc_card")
public class NfcCard {

    @Id
    @NotNull
    private Long nfcId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Sound sound;

    @NotNull
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Plan plan;


    // On met protected car on veut eviter de pouvoir créer une NfcCard vide ça n'aurait pas de sens,
    // De plus on met pas private car JPA crash.
    protected NfcCard() {}

    public NfcCard(Long nfcId, Long customerId, Sound sound, Plan plan) {
        this.nfcId = nfcId;
        this.customerId = customerId;
        this.sound = sound;
        this.plan = plan;
    }

    public Long getNfcId() {
        return nfcId;
    }

    public Sound getSound() {
        return this.sound;
    }

    public Long getCustomerId() { return customerId; }

    public Plan getPlan() { return this.plan; }
}
