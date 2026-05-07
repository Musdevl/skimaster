package fr.univcotedazur.skimaster.monitoring.connectors.externaldto;

import fr.univcotedazur.skimaster.monitoring.entities.GateStatus;

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
