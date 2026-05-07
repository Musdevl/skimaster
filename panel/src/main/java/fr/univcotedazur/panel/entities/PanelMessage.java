package fr.univcotedazur.panel.entities;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class PanelMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant at;

    @Enumerated(EnumType.STRING)
    private PanelSeverity severity;

    @Column(length = 2048)
    private String message;

    protected PanelMessage() {}

    public PanelMessage(Instant at, PanelSeverity severity, String message) {
        this.at = at;
        this.severity = severity;
        this.message = message;
    }

    public Long getId() { return id; }

    public Instant getAt() { return at; }

    public PanelSeverity getSeverity() { return severity; }

    public String getMessage() { return message; }
}
