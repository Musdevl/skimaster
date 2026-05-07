package fr.univ_cotedazur.gate.gate.connector.interfaces;
import fr.univ_cotedazur.gate.gate.entities.Severity;

public interface Skimaster {
    void registerGate(String gateName, String uri, Long domainId);
    void reportIssue(String gateName, String details);
    void informClosing(String gateName, String details);
    void informOpening(String gateName, String details);
    void sendThresholdAlert(String gateId, int currentGauge, int threshold, Severity severity);
}