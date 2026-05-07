package fr.univcotedazur.skimaster.monitoring.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class GateConnection
{
    @Id
    private String id;

    @NotBlank
    private String URI;

    @ManyToOne
    @JoinColumn(name = "domain_id")
    private Domain domain;

    protected GateConnection() {}

    public GateConnection(String id, String URI) {
        this.id = id;
        this.URI = URI;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public String getId() {
        return id;
    }


    public String getURI() {
        return URI;
    }

    public Domain getDomain() { return domain; }
}
