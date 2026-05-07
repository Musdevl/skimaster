package fr.univcotedazur.skimaster.monitoring.components;

import fr.univcotedazur.skimaster.monitoring.connectors.GateProxy;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateDailyReportDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.interfaces.Gate;
import fr.univcotedazur.skimaster.monitoring.dto.report.DomainDailyReportDTO;
import fr.univcotedazur.skimaster.monitoring.dto.report.ResortDailyReportDTO;
import fr.univcotedazur.skimaster.monitoring.entities.*;
import fr.univcotedazur.skimaster.monitoring.interfaces.ReportFinder;
import fr.univcotedazur.skimaster.monitoring.interfaces.ReportProducer;
import fr.univcotedazur.skimaster.monitoring.repositories.DailyReportRepository;
import fr.univcotedazur.skimaster.monitoring.repositories.DomainRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Component
public class ReportRegistry implements ReportFinder, ReportProducer {

    private final DailyReportRepository dailyReportRepository;

    private final DomainRepository domainRepository;
    private final Gate gateProxy;
    private static final Logger LOG = LoggerFactory.getLogger(ReportRegistry.class);

    public ReportRegistry(DailyReportRepository repository, DomainRepository domainRepository, GateProxy gateProxy) {
        this.gateProxy = gateProxy;
        this.domainRepository = domainRepository;
        this.dailyReportRepository = repository;
    }

    @Override
    public ResortDailyReport findByDay(LocalDate day) {
        return dailyReportRepository.findByReportDayWithFullGraph(day)
                .orElseThrow(() -> new RuntimeException("Report not found for day: " + day));
    }

    @Override
    @Transactional
    public void deleteExistingTodayReport() {
        dailyReportRepository.findByReportDay(LocalDate.now())
                .ifPresent(dailyReportRepository::delete);
    }

    @Override
    public ResortDailyReport saveReport(ResortDailyReport report){
        return this.dailyReportRepository.save(report);
    }

    @Override
    @Transactional
    public ResortDailyReport produceTodayReport() {

        LocalDate day = LocalDate.now();

        List<Domain> domainList = domainRepository.findAll();
        List<DomainDailyReport> domainDailyReports = new ArrayList<>();

        Set<Long> skiersIds = new HashSet<>();

        for(Domain domain : domainList){
            DomainDailyReport report = this.produceDomainDailyReport(domain.getName(), domain.getGateConnections());
            domainDailyReports.add(report);
            skiersIds.addAll(report.getSkiersIds());
        }

        return new ResortDailyReport(day, skiersIds, domainDailyReports);
    }

    @Override
    public DomainDailyReport produceDomainDailyReport(String domainName, Set<GateConnection> gateConnections) {

        List<GateDailyReport> gateReports = new ArrayList<>();

        Set<Long> domainSkiersIds = new HashSet<>();

        for(GateConnection gateConnection: gateConnections){
            Optional<GateDailyReportDTO> gateDailyReportDTO = this.gateProxy.requestReport(gateConnection.getURI());
            if(gateDailyReportDTO.isPresent()){
                GateDailyReportDTO dto = gateDailyReportDTO.get();
                GateDailyReport report = new GateDailyReport(dto.gateId(), dto.passages(), dto.openMinutes(), dto.skiers_ids());
                gateReports.add(report);
                domainSkiersIds.addAll(report.getSkiersIds());
            }
            else {
                LOG.warn("Failed to retrieve report on gate {}", gateConnection.getURI());
            }
        }

        return new DomainDailyReport(domainName, domainSkiersIds, gateReports);
    }

    @Override
    public ResortDailyReportDTO toDto(ResortDailyReport report) {
        return new ResortDailyReportDTO(
                report.getDay(),
                report.getTotalNumberOfSkiers(),
                report.getDomainReports().stream().map(this::toDto).toList()
        );
    }

    @Override
    public DomainDailyReportDTO toDto(DomainDailyReport report) {
        return new DomainDailyReportDTO(
                report.getDomainName(),
                report.getNumberOfSkiers(),
                report.getGateReports().stream().map(this::toDto).toList()
        );
    }

    public GateDailyReportDTO toDto(GateDailyReport report) {
        return new GateDailyReportDTO(
                report.getGateId(),
                report.getPassages(),
                report.getOpenedMinutes(),
                report.getSkiersIds()
        );
    }
}
