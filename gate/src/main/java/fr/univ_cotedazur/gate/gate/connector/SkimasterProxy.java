package fr.univ_cotedazur.gate.gate.connector;

import fr.univ_cotedazur.gate.gate.connector.externaldto.GateInformationDTO;
import fr.univ_cotedazur.gate.gate.connector.externaldto.GateRegisterDTO;
import fr.univ_cotedazur.gate.gate.connector.externaldto.ThresholdAlertRequest;
import fr.univ_cotedazur.gate.gate.entities.Severity;
import fr.univ_cotedazur.gate.gate.connector.interfaces.Skimaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class SkimasterProxy implements Skimaster {

    private static final Logger LOG = LoggerFactory.getLogger(SkimasterProxy.class);

    private final String skimasterHostAndPort;
    private final RestClient restClient;

    public SkimasterProxy(@Value("${skimaster.host.baseurl}") String skimasterHostAndPort, RestClient.Builder restClientBuilder) {
        this.skimasterHostAndPort = skimasterHostAndPort;
        this.restClient = restClientBuilder.baseUrl(this.skimasterHostAndPort).build();
    }

    @Override
    public void registerGate(String gateName, String uri, Long domainId) {
        try {
            ResponseEntity<String> responseEntity = restClient.post()
                    .uri("/monitoring/gates/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new GateRegisterDTO(gateName, uri, domainId))
                    .retrieve()
                    .toEntity(String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                LOG.info("Register call successful");
            } else {
                LOG.warn("Register call not successful: Unexpected status {}", responseEntity.getStatusCode());
            }
        } catch (RestClientException e) {
            LOG.error("Register Call error {}", e.getMessage());
        }
    }

    @Override
    public void reportIssue(String gateName, String details) {
        try {
            ResponseEntity<String> responseEntity = restClient.post()
                    .uri("/monitoring/gates/" + gateName + "/issues")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new GateInformationDTO(gateName, details))
                    .retrieve()
                    .toEntity(String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                LOG.info("Emergency call successful");
            } else {
                LOG.warn("Emergency call not successful: Unexpected status {}", responseEntity.getStatusCode());
            }
        } catch (RestClientException e) {
            LOG.error("Emergency Call error {}", e.getMessage());
        }
    }

    @Override
    public void informClosing(String gateName, String details){
        try {
            ResponseEntity<String> responseEntity = restClient.post()
                    .uri("/monitoring/gates/inform-gate-closing")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new GateInformationDTO(gateName, details))
                    .retrieve()
                    .toEntity(String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                LOG.info("Skimaster successfully informed of the closing of the gate");
            } else {
                LOG.warn("Skimaster inform closing call not successful: Unexpected status {}", responseEntity.getStatusCode());
            }
        } catch (RestClientException e) {
            LOG.error("Skimaster inform closing call error {}", e.getMessage());
        }
    }

    @Override
    public void informOpening(String gateName, String details){
        try {
            ResponseEntity<String> responseEntity = restClient.post()
                    .uri("/monitoring/gates/inform-gate-opening")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new GateInformationDTO(gateName, details))
                    .retrieve()
                    .toEntity(String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                LOG.info("Skimaster successfully informed of the opening");
            } else {
                LOG.warn("Skimaster inform opening call not successful: Unexpected status {}", responseEntity.getStatusCode());
            }
        } catch (RestClientException e) {
            LOG.error("Skimaster inform opening call error {}", e.getMessage());
        }
    }

    @Override
    public void sendThresholdAlert(String gateId, int currentGauge, int threshold, Severity severity) {
        try {
            ThresholdAlertRequest request = new ThresholdAlertRequest(gateId, currentGauge, threshold, severity);
            restClient.post()
                    .uri("/monitoring/threshold-alert")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
            LOG.info("Threshold alert sent successfully for gate {}: gauge={}, threshold={}, severity={}", gateId, currentGauge, threshold, severity);
        } catch (RestClientException e) {
            LOG.warn("Failed to send threshold alert for gate {}: {}", gateId, e.getMessage());
        }
    }
}
