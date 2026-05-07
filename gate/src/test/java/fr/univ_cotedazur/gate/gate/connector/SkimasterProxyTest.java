package fr.univ_cotedazur.gate.gate.connector;

import fr.univ_cotedazur.gate.gate.entities.Severity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(SkimasterProxy.class)
class SkimasterProxyTest {

    private final String BASE = "http://localhost:8080";

    @Autowired
    private SkimasterProxy skimasterProxy;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    void registerGate_Success() {
        mockServer.expect(requestTo(BASE + "/monitoring/gates/register"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withSuccess("OK", MediaType.TEXT_PLAIN));

        skimasterProxy.registerGate("Gate1", "http://gate-uri", (long) 1);
        mockServer.verify();
    }

    @Test
    void reportIssue_Success() {
        mockServer.expect(requestTo(BASE + "/monitoring/gates/Gate1/issues"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(jsonPath("$.details").value("Broken"))
                .andRespond(withSuccess());

        skimasterProxy.reportIssue("Gate1", "Broken");
        mockServer.verify();
    }

    @Test
    void informClosing_Success() {
        mockServer.expect(requestTo(BASE + "/monitoring/gates/inform-gate-closing"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withSuccess());

        skimasterProxy.informClosing("Gate1", "End of day");
        mockServer.verify();
    }

    @Test
    void informOpening_Success() {
        mockServer.expect(requestTo(BASE + "/monitoring/gates/inform-gate-opening"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withSuccess());

        skimasterProxy.informOpening("Gate1", "Morning");
        mockServer.verify();
    }

    @Test
    void sendThresholdAlert_Success() {
        mockServer.expect(requestTo(BASE + "/monitoring/threshold-alert"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(jsonPath("$.severity").value("CRITICAL"))
                .andRespond(withSuccess());

        skimasterProxy.sendThresholdAlert("Gate1", 10, 5, Severity.CRITICAL);
        mockServer.verify();
    }

    @Test
    void registerGate_Failure_Logged() {
        mockServer.expect(requestTo(BASE + "/monitoring/gates/register"))
                .andRespond(withServerError());

        skimasterProxy.registerGate("Gate1", "uri", (long) 1);
        mockServer.verify();
    }

    @Test
    void informOpening_UnexpectedStatus_Logged() {
        mockServer.expect(requestTo(BASE + "/monitoring/gates/inform-gate-opening"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        skimasterProxy.informOpening("Gate1", "details");
        mockServer.verify();
    }
}