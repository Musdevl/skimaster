package fr.univ_cotedazur.gate.gate.dto;

import java.util.Set;

public record GateDailyReportDTO(String gateId, int passages, double openMinutes, Set<Long> skiers_ids) {}
