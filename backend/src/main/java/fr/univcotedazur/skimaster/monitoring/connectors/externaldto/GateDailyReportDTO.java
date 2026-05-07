package fr.univcotedazur.skimaster.monitoring.connectors.externaldto;

import java.util.Set;

public record GateDailyReportDTO(String gateId, int passages, double openMinutes, Set<Long> skiers_ids) {}
