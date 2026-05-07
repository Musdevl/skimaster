package fr.univcotedazur.skimaster.monitoring.interfaces;

import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateStatusDTO;
import fr.univcotedazur.skimaster.monitoring.dto.gate.ThresholdsDTO;
import fr.univcotedazur.skimaster.monitoring.entities.GateConnection;

public interface GateProcessor {
    void configureThresholds(String gateId, ThresholdsDTO thresholds);
    GateStatusDTO getGateStatus(GateConnection gateConnection);
    GateConnection getGateConnection(String gateId);
    void openManual(String gateId, String details);
    void closeManual(String gateId, String details);
}
