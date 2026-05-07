package fr.univcotedazur.skimaster.monitoring.entities;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"report_day"}))
public class ResortDailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_day", nullable = false)
    private LocalDate reportDay;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "resort_report_skiers", joinColumns = @JoinColumn(name = "resort_report_id"))
    private Set<Long> skiers_ids;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "resort_report_id")
    private List<DomainDailyReport> domainReports = new ArrayList<>();

    protected ResortDailyReport() {}

    public ResortDailyReport(LocalDate day, Set<Long> skiers_ids, List<DomainDailyReport> domainReports) {
        this.reportDay = day;
        this.skiers_ids = skiers_ids;
        this.domainReports = domainReports;
    }

    public Long getId() { return id; }

    public LocalDate getDay() { return reportDay; }

    public void setDay(LocalDate day) { this.reportDay = day; }

    public Set<Long> getSkiersIds() { return skiers_ids; }

    public int getTotalNumberOfSkiers() { return this.skiers_ids.size(); }

    public List<DomainDailyReport> getDomainReports() { return domainReports; }

}
