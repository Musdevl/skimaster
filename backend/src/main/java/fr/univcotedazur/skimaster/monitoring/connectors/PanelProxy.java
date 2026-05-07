package fr.univcotedazur.skimaster.monitoring.connectors;


import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.PanelGateStatusDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.PanelStatusDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.interfaces.Panel;
import fr.univcotedazur.skimaster.monitoring.dto.panel.PanelMessageDTO;
import fr.univcotedazur.skimaster.monitoring.entities.GateStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import javax.swing.*;
import java.util.Optional;

@Component
public class PanelProxy implements Panel {

    private static final Logger LOG = LoggerFactory.getLogger(PanelProxy.class);

    private final RestClient restClient;

    public PanelProxy(RestClient.Builder restClientBuilder){
        this.restClient = restClientBuilder.baseUrl("").build();
    }

    @Override
    public Optional<PanelStatusDTO> read(String panelUri) {
        try {
            ResponseEntity<PanelStatusDTO> responseEntity = restClient.get()
                    .uri(panelUri + "/panel").retrieve().toEntity(PanelStatusDTO.class);
            if (responseEntity.getBody() != null && responseEntity.getStatusCode() == HttpStatus.OK) {
                return Optional.of(responseEntity.getBody());
            } else {
                LOG.warn("Panel Message reading not successful: Unexpected response status " + responseEntity.getStatusCode());
            }
        } catch (RestClientException e) { // Catch Exceptions sent on 4XX and 5XX HTTP status codes
            LOG.warn("Panel message reading not successful: Exception during REST call to panel " + panelUri
                    + " with exception " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<PanelStatusDTO> write(String panelUri, PanelMessageDTO messageDTO) {
        try {
            ResponseEntity<PanelStatusDTO> responseEntity = restClient.put()
                    .uri(panelUri + "/panel/message")
                    .body(messageDTO)
                    .retrieve()
                    .toEntity(PanelStatusDTO.class);
            if (responseEntity.getBody() != null && responseEntity.getStatusCode() == HttpStatus.OK) {
                return Optional.of(responseEntity.getBody());
            } else {
                LOG.warn("Panel Message writing not successful: Unexpected response status " + responseEntity.getStatusCode());
            }
        } catch (RestClientException e) { // Catch Exceptions sent on 4XX and 5XX HTTP status codes
            LOG.warn("Panel message writing not successful: Exception during REST call to panel " + panelUri
                    + " with exception " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<PanelGateStatusDTO> updateGateStatus(String panelUri, String gateName, GateStatus status, String detail){

        try{
            ResponseEntity<PanelGateStatusDTO> responseEntity = restClient.put()
                    .uri(panelUri + "/panel/gate")
                    .body(new PanelGateStatusDTO(gateName, status, detail))
                    .retrieve()
                    .toEntity(PanelGateStatusDTO.class);
            if(responseEntity.getBody() != null && responseEntity.getStatusCode() == HttpStatus.OK){
                return Optional.of(responseEntity.getBody());
            } else {
                LOG.warn("Panel gate-status update failed: Unexcepted response status " + responseEntity.getStatusCode());
                return Optional.empty();
            }

        }
        catch (RestClientException e){
            LOG.warn("Panel gate-status updating failed: Exception during REST Call to panel " + panelUri + "with exception: " + e.getMessage());
        }

        return Optional.empty();

    }

    @Override
    public Optional<String> addGateStatus(String panelUri, String gateName, GateStatus status, String detail){
        try{
            ResponseEntity<PanelGateStatusDTO> responseEntity = restClient.post()
                    .uri(panelUri +"/panel/gate")
                    .body(new PanelGateStatusDTO(gateName, status, detail))
                    .retrieve()
                    .toEntity(PanelGateStatusDTO.class);
            if(responseEntity.getBody() != null && responseEntity.getStatusCode() == HttpStatus.OK){
                LOG.warn("Panel Gate Status saved successfully");
                return Optional.of("Panel Gate Status saved successfully");
            } else {
                LOG.warn("Panel Gate Status save failed: Unexcepted response status " + responseEntity.getStatusCode());
            }
        } catch (RestClientException e){
            LOG.warn("Panel gate status save failed: Exception during REST Call to panel " + panelUri);
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> removeGateStatus(String panelUri, String gateName){
        try{
            ResponseEntity<String> responseEntity = restClient.delete()
                    .uri(panelUri +"/panel/gate/" + gateName)
                    .retrieve()
                    .toEntity(String.class);
            if(responseEntity.getBody() != null && responseEntity.getStatusCode() == HttpStatus.OK){
                LOG.warn("Panel Gate Status removed successfully");
                return Optional.of(responseEntity.getBody());
            } else {
                LOG.warn("Panel Gate Status removal failed: Unexcepted response status " + responseEntity.getStatusCode());
            }
        } catch (RestClientException e){
            LOG.warn("Panel gate status removal failed: Exception during REST Call to panel " + panelUri + " " + e.getMessage());
        }
        return Optional.empty();
    }
}
