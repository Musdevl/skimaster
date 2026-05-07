package fr.univcotedazur.skimaster.monitoring.connectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.PanelGateStatusDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.PanelStatusDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.interfaces.Panel;
import fr.univcotedazur.skimaster.monitoring.dto.panel.PanelMessageDTO;
import fr.univcotedazur.skimaster.monitoring.entities.GateStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(Panel.class)
class PanelProxyTest {

    private static final String BASE = "http://panel:8080";

    @Autowired ObjectMapper objectMapper;
    @Autowired PanelProxy panelProxy;
    @Autowired MockRestServiceServer mockServer;

    // read

    @Test
    void read_ok() throws Exception {
        mockServer.expect(requestTo(BASE + "/panel")).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(new PanelStatusDTO(null, null))));
        assertTrue(panelProxy.read(BASE).isPresent());
    }

    @Test
    void read_fail() {
        mockServer.expect(requestTo(BASE + "/panel")).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        assertTrue(panelProxy.read(BASE).isEmpty());
    }

    @Test
    void read_serverError() {
        mockServer.expect(requestTo(BASE + "/panel")).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertTrue(panelProxy.read(BASE).isEmpty());
    }

    // write

    @Test
    void write_ok() throws Exception {
        mockServer.expect(requestTo(BASE + "/panel/message")).andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(new PanelStatusDTO(null, null))));
        assertTrue(panelProxy.write(BASE, new PanelMessageDTO(null, null, null)).isPresent());
    }

    @Test
    void write_fail() {
        mockServer.expect(requestTo(BASE + "/panel/message")).andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        assertTrue(panelProxy.write(BASE, new PanelMessageDTO(null, null, null)).isEmpty());
    }

    @Test
    void write_serverError() {
        mockServer.expect(requestTo(BASE + "/panel/message")).andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertTrue(panelProxy.write(BASE, new PanelMessageDTO(null, null, null)).isEmpty());
    }

    // updateGateStatus

    @Test
    void updateGateStatus_ok() throws Exception {
        mockServer.expect(requestTo(BASE + "/panel/gate")).andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(new PanelGateStatusDTO("gate1", GateStatus.OPENED, "ok"))));
        assertTrue(panelProxy.updateGateStatus(BASE, "gate1", GateStatus.OPENED, "ok").isPresent());
    }

    @Test
    void updateGateStatus_fail() {
        mockServer.expect(requestTo(BASE + "/panel/gate")).andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        assertTrue(panelProxy.updateGateStatus(BASE, "gate1", GateStatus.OPENED, "ok").isEmpty());
    }

    @Test
    void updateGateStatus_serverError() {
        mockServer.expect(requestTo(BASE + "/panel/gate")).andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertTrue(panelProxy.updateGateStatus(BASE, "gate1", GateStatus.OPENED, "ok").isEmpty());
    }

    // addGateStatus

    @Test
    void addGateStatus_ok() throws Exception {
        mockServer.expect(requestTo(BASE + "/panel/gate")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(new PanelGateStatusDTO("gate1", GateStatus.OPENED, "ok"))));
        assertTrue(panelProxy.addGateStatus(BASE, "gate1", GateStatus.OPENED, "ok").isPresent());
    }

    @Test
    void addGateStatus_fail() {
        mockServer.expect(requestTo(BASE + "/panel/gate")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        assertTrue(panelProxy.addGateStatus(BASE, "gate1", GateStatus.OPENED, "ok").isEmpty());
    }

    @Test
    void addGateStatus_serverError() {
        mockServer.expect(requestTo(BASE + "/panel/gate")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertTrue(panelProxy.addGateStatus(BASE, "gate1", GateStatus.OPENED, "ok").isEmpty());
    }

    // removeGateStatus

    @Test
    void removeGateStatus_ok() {
        mockServer.expect(requestTo(BASE + "/panel/gate/gate1")).andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body("removed"));
        assertTrue(panelProxy.removeGateStatus(BASE, "gate1").isPresent());
    }

    @Test
    void removeGateStatus_fail() {
        mockServer.expect(requestTo(BASE + "/panel/gate/gate1")).andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        assertTrue(panelProxy.removeGateStatus(BASE, "gate1").isEmpty());
    }

    @Test
    void removeGateStatus_serverError() {
        mockServer.expect(requestTo(BASE + "/panel/gate/gate1")).andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertTrue(panelProxy.removeGateStatus(BASE, "gate1").isEmpty());
    }
}