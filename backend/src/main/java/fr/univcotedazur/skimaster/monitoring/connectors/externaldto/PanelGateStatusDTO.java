package fr.univcotedazur.skimaster.monitoring.connectors.externaldto;

import fr.univcotedazur.skimaster.monitoring.entities.GateStatus;

public record PanelGateStatusDTO(String gateName, GateStatus status, String detail){}
