package fr.univcotedazur.panel.connector;

import fr.univcotedazur.panel.dto.PanelGateStatusDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@RestClientTest(SkimasterProxy.class)
class SkimasterProxyTest {

    private static final String BASE = "http://localhost:8080";

    @Autowired
    private SkimasterProxy skimasterProxy;

    @Autowired
    private MockRestServiceServer mockServer;

    // registerPanel
    @Test
    void registerPanel_Success() {
        mockServer.expect(requestTo(BASE + "/monitoring/panels/register"))
                .andExpect(method(POST))
                .andRespond(withSuccess("OK", MediaType.TEXT_PLAIN));

        String result = skimasterProxy.registerPanel("panel1", "http://localhost:8081");

        assertEquals("Register call successful", result);
        mockServer.verify();
    }

    @Test
    void registerPanel_UnexpectedStatus() {
        mockServer.expect(requestTo(BASE + "/monitoring/panels/register"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        String result = skimasterProxy.registerPanel("panel1", "uri");

        assertEquals("Register call error", result);
    }

    @Test
    void registerPanel_Exception() {
        mockServer.expect(requestTo(BASE + "/monitoring/panels/register"))
                .andRespond(withServerError());

        String result = skimasterProxy.registerPanel("panel1", "uri");

        assertEquals("Register call error", result);
    }

    // fetchGateStatus
    @Test
    void fetchGateStatus_Success() {
        String jsonResponse = "[{\"gateName\":\"GateA\",\"status\":\"OPENED\",\"detail\":\"Ok\"}]";

        mockServer.expect(requestTo(BASE + "/monitoring/panel-gate-status"))
                .andExpect(method(GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        List<PanelGateStatusDTO> result = skimasterProxy.fetchGateStatus();

        assertEquals("GateA", result.get(0).gateName());
    }

    @Test
    void fetchGateStatus_ErrorResponse_ReturnsEmptyList() {
        mockServer.expect(requestTo(BASE + "/monitoring/panel-gate-status"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        List<PanelGateStatusDTO> result = skimasterProxy.fetchGateStatus();

        assertTrue(result.isEmpty());
    }

    @Test
    void fetchGateStatus_Exception_ReturnsEmptyList() {
        mockServer.expect(requestTo(BASE + "/monitoring/panel-gate-status"))
                .andRespond(withServerError());

        List<PanelGateStatusDTO> result = skimasterProxy.fetchGateStatus();

        assertTrue(result.isEmpty());
    }
}