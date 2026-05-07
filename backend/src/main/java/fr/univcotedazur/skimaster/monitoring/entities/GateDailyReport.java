package fr.univcotedazur.skimaster.monitoring.entities;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class GateDailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gateId;
    private int passages;
    private double openedMinutes;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "gate_report_skiers", joinColumns = @JoinColumn(name = "gate_report_id"))
    private Set<Long> skiers_ids;

    protected GateDailyReport() {}

    public GateDailyReport(String gateId, int passages, double openedMinutes, Set<Long> skiers_ids) {
        this.gateId = gateId;
        this.passages = passages;
        this.openedMinutes = openedMinutes;
        this.skiers_ids = skiers_ids;
    }

    public String getGateId() { return this.gateId; }
    public int getPassages() { return this.passages; }
    public double getOpenedMinutes() { return this.openedMinutes; }
    public Set<Long> getSkiersIds() { return this.skiers_ids; }
}