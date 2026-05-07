package fr.univcotedazur.skimaster.monitoring.dto.report;

import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateDailyReportDTO;

import java.util.List;

public record DomainDailyReportDTO(String domainName, long totalSkiers, List<GateDailyReportDTO> gateReports) {}
