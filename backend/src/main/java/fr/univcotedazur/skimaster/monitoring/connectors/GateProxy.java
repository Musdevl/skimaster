package fr.univcotedazur.skimaster.monitoring.connectors;

import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.DetailDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.AlertThresholdDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateStatusDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.interfaces.Gate;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateDailyReportDTO;import fr.univcotedazur.skimaster.nfc.dto.NFCCardDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;

@Component
public class GateProxy implements Gate {
    private static final Logger LOG = LoggerFactory.getLogger(GateProxy.class);

    private final RestClient restClient;

    public GateProxy(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("").build();
    }

    @Override
    public Optional<String> closeGate(String gateConnectionUri, String details){
        try {
            ResponseEntity<String> responseEntity = restClient.post()
                    .uri(gateConnectionUri + "/close").body(new DetailDTO(details)).retrieve().toEntity(String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return Optional.of("Gate closed Successfully");
            } else {
                LOG.warn("Gate closing not successful: Unexpected response status " + responseEntity.getStatusCode());
            }
        } catch (RestClientException e) { // Catch Exceptions sent on 4XX and 5XX HTTP status codes
            LOG.warn("Gate close not successful: Exception during REST call to Gate for gate " + gateConnectionUri
                    + " with exception " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> openGate(String gateConnectionUri, String details){
        try {
            ResponseEntity<String> responseEntity = restClient.post()
                    .uri(gateConnectionUri + "/open").body(new DetailDTO(details)).retrieve().toEntity(String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return Optional.of("Gate opened Successfully");
            } else {
                LOG.warn("Gate opening not successful: Unexpected response status " + responseEntity.getStatusCode());
            }
        } catch (RestClientException e) { // Catch Exceptions sent on 4XX and 5XX HTTP status codes
            LOG.warn("Gate opening not successful: Exception during REST call to Gate for gate " + gateConnectionUri
                    + " with exception " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> setAlertThresholds(String gateConnectionUri, int warning_threshold, int critical_threshold){
        try {
            ResponseEntity<String> responseEntity = restClient.put()
                    .uri(gateConnectionUri + "/alert-thresholds")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new AlertThresholdDTO(warning_threshold, critical_threshold))
                    .retrieve().toEntity(String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return Optional.of("Threshold updated successfully");
            } else {
                LOG.warn("Threshold updating not successful: Unexpected response status " + responseEntity.getStatusCode());
            }
        } catch (RestClientException e) { // Catch Exceptions sent on 4XX and 5XX HTTP status codes
            LOG.warn("Threshold updating not successful: Exception during REST call to Gate for gate " + gateConnectionUri
                    + " with exception " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> refreshCards(String gateConnectionUri, List<NFCCardDTO> nfcCardDTOs){
        try {
            ResponseEntity<String> responseEntity = restClient.put()
                    .uri(gateConnectionUri + "/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(nfcCardDTOs)
                    .retrieve().toEntity(String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return Optional.of("NFC Card refreshed successfully");
            } else {
                LOG.warn("NFC Card refreshing not successful: Unexpected response status " + responseEntity.getStatusCode());
            }
        } catch (RestClientException e) { // Catch Exceptions sent on 4XX and 5XX HTTP status codes
            LOG.warn("NFC Card refreshing not successful: Exception during REST call to Gate for gate " + gateConnectionUri
                    + " with exception " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<NFCCardDTO>> requestSuperCards(String gateConnectionUri) {
        try{
            ResponseEntity<List<NFCCardDTO>> responseEntity = restClient.get()
                    .uri(gateConnectionUri + "/daily-super-card")
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<NFCCardDTO>>() {});
            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
                return Optional.of(responseEntity.getBody());
            } else {
                LOG.warn("NFC Supercard retrieving not successful: Unexpected response status " + responseEntity.getStatusCode());
            }
        } catch (RestClientException e){
            LOG.warn("NFC Super Card retrieving not successful: Exception during REST call to Gate for gate " + gateConnectionUri
                    + " with exception " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<GateDailyReportDTO> requestReport(String gateConnectionUri) {
        try{
            ResponseEntity<GateDailyReportDTO> responseEntity = restClient.get()
                    .uri(gateConnectionUri + "/request-report")
                    .retrieve()
                    .toEntity(GateDailyReportDTO.class);
            if(responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null){
                return Optional.of(responseEntity.getBody());
            } else {
                LOG.warn("Gate Status retrieving not successful: Unexpected response status " + responseEntity.getStatusCode());
            }

        } catch (RestClientException e){
            LOG.warn("Gate status retrieving not successful: Exception during REST call to Gate for gate " + gateConnectionUri
                    + " with exception " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<GateStatusDTO> requestStatus(String gateConnectionUri){
        try{
            ResponseEntity<GateStatusDTO> responseEntity = restClient.get()
                    .uri(gateConnectionUri + "/status")
                    .retrieve()
                    .toEntity(GateStatusDTO.class);
        if(responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null){
            return Optional.of(responseEntity.getBody());
        } else {
            LOG.warn("Gate Status retrieving not successful: Unexpected response status " + responseEntity.getStatusCode());
        }

        } catch (RestClientException e){
            LOG.warn("Gate status retrieving not successful: Exception during REST call to Gate for gate " + gateConnectionUri
                    + " with exception " + e.getMessage());
        }
        return Optional.empty();
    }

    public void addCard(String uri, NFCCardDTO newCard) {
        try {
            ResponseEntity<String> responseEntity = restClient.post()
                    .uri(uri + "/add-card")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(newCard)
                    .retrieve().toEntity(String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                LOG.info("Card added successfully");
            } else {
                LOG.warn("Card adding not successful: Unexpected response status " + responseEntity.getStatusCode());
            }
        }
        catch (RestClientException e){
            LOG.warn("Card adding not successful: Exception during REST call to Gate for gate " + uri
                    + " with exception " + e.getMessage());
        }
    }
}
