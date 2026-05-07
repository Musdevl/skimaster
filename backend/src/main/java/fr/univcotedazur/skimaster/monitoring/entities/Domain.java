package fr.univcotedazur.skimaster.monitoring.entities;

import fr.univcotedazur.skimaster.customer.entities.Plan;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Domain {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    @NotBlank
    private String name;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "domain")
    private Set<GateConnection> gateConnections = new HashSet<>();


    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(name = "plan")
    private Set<Plan> plans = new HashSet<>();

    public Domain() {}

    public Domain(String name) {
        this.name = name;
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

    public Set<Plan> getPlans() {
        return plans;
    }

    public Set<GateConnection> getGateConnections() {
        return gateConnections;
    }

    public void addGateConnection(GateConnection gc) {
        this.gateConnections.add(gc);
    }

    public void addPlan(Plan plan){
        this.plans.add(plan);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Domain other)) return false;
        return Objects.equals(id, other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
