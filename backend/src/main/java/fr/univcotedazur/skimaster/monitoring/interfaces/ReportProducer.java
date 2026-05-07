package fr.univcotedazur.skimaster.monitoring.interfaces;

import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateDailyReportDTO;
import fr.univcotedazur.skimaster.monitoring.dto.report.DomainDailyReportDTO;
import fr.univcotedazur.skimaster.monitoring.dto.report.ResortDailyReportDTO;
import fr.univcotedazur.skimaster.monitoring.entities.DomainDailyReport;
import fr.univcotedazur.skimaster.monitoring.entities.GateConnection;
import fr.univcotedazur.skimaster.monitoring.entities.GateDailyReport;
import fr.univcotedazur.skimaster.monitoring.entities.ResortDailyReport;

import java.time.LocalDate;
import java.util.Set;

public interface ReportProducer {

    ResortDailyReport produceTodayReport();
    DomainDailyReport produceDomainDailyReport(String domainName, Set<GateConnection> gateConnections);

    ResortDailyReportDTO toDto(ResortDailyReport report);
    DomainDailyReportDTO toDto(DomainDailyReport report);
    GateDailyReportDTO toDto(GateDailyReport report);

}
