package fr.univ_cotedazur.gate.gate.dto;

import fr.univ_cotedazur.gate.gate.entities.GateStatus;

public record GateStatusDTO(String gateId,
                            GateStatus status,
                            double openedMinutes,
                            int warningThreshold,
                            int criticalThreshold,
                            long dailyPassageCount,
                            long lastMinutePassage,
                            String detail,
                            Long domainId,
                            String uri) {
}
