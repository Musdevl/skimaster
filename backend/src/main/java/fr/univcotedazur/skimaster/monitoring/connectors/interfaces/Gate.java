package fr.univcotedazur.skimaster.monitoring.connectors.interfaces;

import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateStatusDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateDailyReportDTO;import fr.univcotedazur.skimaster.nfc.dto.NFCCardDTO;

import java.util.List;
import java.util.Optional;

public interface Gate {

    Optional<String> closeGate(String gateConnectionUri, String details);
    Optional<String> openGate(String gateConnectionUri, String details);

    Optional<String> setAlertThresholds(String gateConnectionUri, int warning_threshold, int critical_threshold);
    Optional<String> refreshCards(String gateConnectionUri, List<NFCCardDTO> cardDTOS);

    Optional<GateStatusDTO> requestStatus(String gateConnectionUri);
    Optional<List<NFCCardDTO>> requestSuperCards(String gateConnectionUri);

    Optional<GateDailyReportDTO> requestReport(String gateConnectionUri);

}
