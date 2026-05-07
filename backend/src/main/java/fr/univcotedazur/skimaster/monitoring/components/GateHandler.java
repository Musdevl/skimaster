package fr.univcotedazur.skimaster.monitoring.components;

import fr.univcotedazur.skimaster.monitoring.connectors.GateProxy;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateStatusDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.interfaces.Gate;
import fr.univcotedazur.skimaster.monitoring.dto.gate.ThresholdsDTO;
import fr.univcotedazur.skimaster.monitoring.entities.GateConnection;
import fr.univcotedazur.skimaster.monitoring.exceptions.FailedToGetGateStatusException;
import fr.univcotedazur.skimaster.monitoring.exceptions.GateNotFoundException;
import fr.univcotedazur.skimaster.monitoring.exceptions.InvalidThresholdsException;
import fr.univcotedazur.skimaster.monitoring.interfaces.GateProcessor;
import fr.univcotedazur.skimaster.monitoring.repositories.GateConnectionRepository;
import org.springframework.stereotype.Component;

@Component
public class GateHandler implements GateProcessor {

    private final GateConnectionRepository gateConnectionRepository;
    private final Gate gateProxy;

    public GateHandler(GateConnectionRepository gateConnectionRepository, GateProxy gateProxy){
        this.gateConnectionRepository = gateConnectionRepository;
        this.gateProxy = gateProxy;
    }

    @Override
    public void configureThresholds(String gateId, ThresholdsDTO thresholds) {
        if (thresholds.warning() < 0 || thresholds.critical() < 0) {
            throw new InvalidThresholdsException("Thresholds must be >= 0");
        }
        if (thresholds.critical() > 0 && thresholds.warning() > thresholds.critical()) {
            throw new InvalidThresholdsException("warning threshold must be <= critical threshold");
        }

        GateConnection gate = gateConnectionRepository.findById(gateId)
                .orElseThrow(() -> new GateNotFoundException("Gate not found: " + gateId));

        gateProxy.setAlertThresholds(gate.getURI(), thresholds.warning(), thresholds.critical());
    }

    @Override
    public GateStatusDTO getGateStatus(GateConnection gateConnection){
        return gateProxy.requestStatus(gateConnection.getURI())
                .orElseThrow(() -> new FailedToGetGateStatusException("Failed to get gate status for gate connection: " + gateConnection.getId()));
    }

    @Override
    public GateConnection getGateConnection(String gateId){
        return gateConnectionRepository.findById(gateId)
                .orElseThrow(() -> new GateNotFoundException("Gate not found: " + gateId));
    }

    @Override
    public void openManual(String gateId, String details){
        GateConnection gate = gateConnectionRepository.findById(gateId)
                .orElseThrow(() -> new GateNotFoundException("Gate not found: " + gateId));
        gateProxy.openGate(gate.getURI(), details);
    }

    public void closeManual(String gateId, String details){
        GateConnection gate = gateConnectionRepository.findById(gateId)
                .orElseThrow(() -> new GateNotFoundException("Gate not found: " + gateId));
        gateProxy.closeGate(gate.getURI(), details);
    }
}
