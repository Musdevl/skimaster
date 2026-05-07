package fr.univcotedazur.skimaster.monitoring.dto.report;

import java.time.LocalDate;
import java.util.List;

public record ResortDailyReportDTO (LocalDate day, int numberOfSkiers, List<DomainDailyReportDTO> domainReports) {}
