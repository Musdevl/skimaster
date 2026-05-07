package fr.univ_cotedazur.gate.gate.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "nfc_card_scan")
public class NfcCardScan {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long nfcId; // référence à la NfcCard scannée

    @Column(nullable = false, updatable = false)
    private LocalDateTime scannedAt;

    @PrePersist
    protected void onCreate() {
        this.scannedAt = LocalDateTime.now();
    }

    public NfcCardScan() {}

    public NfcCardScan(Long nfcId) {
        this.nfcId = nfcId;
    }

    public Long getId() { return this.id; }

    public LocalDateTime getScanDate(){ return this.scannedAt; }

}
