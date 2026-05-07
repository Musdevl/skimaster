package fr.univcotedazur.skimaster.monitoring.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;import java.util.Set;
@Entity
public class DomainDailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "domain_report_skiers", joinColumns = @JoinColumn(name = "domain_report_id"))
    private Set<Long> skiers_ids;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "domain_report_id")
    private List<GateDailyReport> gateReports = new ArrayList<>();

    protected DomainDailyReport() {}

    public DomainDailyReport(String domainName, Set<Long> skiers_ids, List<GateDailyReport> gateReports) {
        this.name = domainName;
        this.skiers_ids = skiers_ids;
        this.gateReports = gateReports;
    }

    public Long getId() { return id; }

    public Set<Long> getSkiersIds() { return skiers_ids; }

    public int getNumberOfSkiers() { return skiers_ids.size(); }

    public List<GateDailyReport> getGateReports() { return gateReports; }
    
    public String getDomainName(){ return this.name; }
}
