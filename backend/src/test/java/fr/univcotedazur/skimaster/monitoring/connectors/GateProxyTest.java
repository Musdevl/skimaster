package fr.univcotedazur.skimaster.monitoring.connectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univcotedazur.skimaster.customer.entities.Plan;
import fr.univcotedazur.skimaster.customer.entities.Sound;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateDailyReportDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateStatusDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.interfaces.Gate;
import fr.univcotedazur.skimaster.monitoring.entities.GateStatus;
import fr.univcotedazur.skimaster.nfc.dto.NFCCardDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(Gate.class)
class GateProxyTest {

    private static final String BASE = "http://gate:8080";
    private static final NFCCardDTO CARD = new NFCCardDTO((long) 1, (long) 1, Sound.LOW_SOUND, Plan.BASIC_PLAN);

    @Autowired ObjectMapper objectMapper;
    @Autowired GateProxy gateProxy;
    @Autowired MockRestServiceServer mockServer;

    // closeGate

    @Test
    void closeGate_ok() {
        mockServer.expect(requestTo(BASE + "/close")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body("ok"));
        assertTrue(gateProxy.closeGate(BASE, "x").isPresent());
    }

    @Test
    void closeGate_fail() {
        mockServer.expect(requestTo(BASE + "/close")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        assertTrue(gateProxy.closeGate(BASE, "x").isEmpty());
    }

    @Test
    void closeGate_serverError() {
        mockServer.expect(requestTo(BASE + "/close")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertTrue(gateProxy.closeGate(BASE, "x").isEmpty());
    }

    // openGate

    @Test
    void openGate_ok() {
        mockServer.expect(requestTo(BASE + "/open")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body("ok"));
        assertTrue(gateProxy.openGate(BASE, "x").isPresent());
    }

    @Test
    void openGate_fail() {
        mockServer.expect(requestTo(BASE + "/open")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        assertTrue(gateProxy.openGate(BASE, "x").isEmpty());
    }

    @Test
    void openGate_serverError() {
        mockServer.expect(requestTo(BASE + "/open")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertTrue(gateProxy.openGate(BASE, "x").isEmpty());
    }

    // addCard

    @Test
    void addCard_ok() throws Exception {
        mockServer.expect(requestTo(BASE + "/add-card")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body("ok"));
        gateProxy.addCard(BASE, CARD);
        mockServer.verify();
    }

    @Test
    void addCard_fail() throws Exception {
        mockServer.expect(requestTo(BASE + "/add-card")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        gateProxy.addCard(BASE, CARD);
        mockServer.verify();
    }

    @Test
    void addCard_serverError() throws Exception {
        mockServer.expect(requestTo(BASE + "/add-card")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        gateProxy.addCard(BASE, CARD);
        mockServer.verify();
    }

    // setAlertThresholds

    @Test
    void setAlertThresholds_ok() {
        mockServer.expect(requestTo(BASE + "/alert-thresholds")).andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body("ok"));
        assertTrue(gateProxy.setAlertThresholds(BASE, 10, 20).isPresent());
    }

    @Test
    void setAlertThresholds_fail() {
        mockServer.expect(requestTo(BASE + "/alert-thresholds")).andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        assertTrue(gateProxy.setAlertThresholds(BASE, 10, 20).isEmpty());
    }

    @Test
    void setAlertThresholds_serverError() {
        mockServer.expect(requestTo(BASE + "/alert-thresholds")).andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertTrue(gateProxy.setAlertThresholds(BASE, 10, 20).isEmpty());
    }

    // refreshCards

    @Test
    void refreshCards_ok() throws Exception {
        mockServer.expect(requestTo(BASE + "/refresh")).andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body("ok"));
        assertTrue(gateProxy.refreshCards(BASE, List.of(CARD)).isPresent());
    }

    @Test
    void refreshCards_fail() {
        mockServer.expect(requestTo(BASE + "/refresh")).andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        assertTrue(gateProxy.refreshCards(BASE, List.of()).isEmpty());
    }

    @Test
    void refreshCards_serverError() {
        mockServer.expect(requestTo(BASE + "/refresh")).andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertTrue(gateProxy.refreshCards(BASE, List.of()).isEmpty());
    }

    // requestSuperCards

    @Test
    void requestSuperCards_ok() throws Exception {
        mockServer.expect(requestTo(BASE + "/daily-super-card")).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(List.of(CARD))));
        assertTrue(gateProxy.requestSuperCards(BASE).isPresent());
    }

    @Test
    void requestSuperCards_fail() {
        mockServer.expect(requestTo(BASE + "/daily-super-card")).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        assertTrue(gateProxy.requestSuperCards(BASE).isEmpty());
    }

    @Test
    void requestSuperCards_serverError() {
        mockServer.expect(requestTo(BASE + "/daily-super-card")).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertTrue(gateProxy.requestSuperCards(BASE).isEmpty());
    }

    // requestReport

    @Test
    void requestReport_ok() throws Exception {
        mockServer.expect(requestTo(BASE + "/request-report")).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(new GateDailyReportDTO("g1", 0, 10, Set.of()))));
        assertTrue(gateProxy.requestReport(BASE).isPresent());
    }

    @Test
    void requestReport_fail() {
        mockServer.expect(requestTo(BASE + "/request-report")).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        assertTrue(gateProxy.requestReport(BASE).isEmpty());
    }

    @Test
    void requestReport_serverError() {
        mockServer.expect(requestTo(BASE + "/request-report")).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertTrue(gateProxy.requestReport(BASE).isEmpty());
    }

    // requestStatus

    @Test
    void requestStatus_ok() throws Exception {
        mockServer.expect(requestTo(BASE + "/status")).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(new GateStatusDTO("g1", GateStatus.OPENED, 10, 10, 10, 10L, 10, "", 10L, ""))));
        assertTrue(gateProxy.requestStatus(BASE).isPresent());
    }

    @Test
    void requestStatus_fail() {
        mockServer.expect(requestTo(BASE + "/status")).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        assertTrue(gateProxy.requestStatus(BASE).isEmpty());
    }

    @Test
    void requestStatus_serverError() {
        mockServer.expect(requestTo(BASE + "/status")).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertTrue(gateProxy.requestStatus(BASE).isEmpty());
    }
}