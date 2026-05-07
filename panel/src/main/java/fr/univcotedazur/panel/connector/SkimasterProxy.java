package fr.univcotedazur.panel.connector;

import fr.univcotedazur.panel.connector.interfaces.Skimaster;
import fr.univcotedazur.panel.dto.PanelGateStatusDTO;
import fr.univcotedazur.panel.dto.PanelRegisterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

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
    public String registerPanel(String panelId, String uri) {
        try {
            LOG.warn("---------------------- " + panelId, uri);
            ResponseEntity<String> responseEntity = restClient.post()
                    .uri("/monitoring/panels/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new PanelRegisterDTO(panelId, uri))
                    .retrieve()
                    .toEntity(String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                LOG.info("Register call successful");
                return "Register call successful";
            } else {
                LOG.warn("Register call not successful: Unexpected status {}", responseEntity.getStatusCode());
                return "Register call not successful";
            }
        } catch (RestClientException e) {
            LOG.error("Register Call error {}", e.getMessage());
            return "Register call error";
        }
    }

    @Override
    public List<PanelGateStatusDTO> fetchGateStatus(){
        try{
            LOG.warn("Panel gate status retrieving");
            ResponseEntity<List<PanelGateStatusDTO>> responseEntity = restClient.get()
                    .uri("/monitoring/panel-gate-status")
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<PanelGateStatusDTO>>() {});

            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
                return responseEntity.getBody();
            } else {
                LOG.warn("Panel gate-status retrieving not successful: Unexpected response status " + responseEntity.getStatusCode());
            }
        } catch (RestClientException e){
            LOG.warn("Panel gate-status retrieving not successful: Exception during REST call to skimaster"
                    + " with exception " + e.getMessage());
        }

        return new ArrayList<>();
    }
}
