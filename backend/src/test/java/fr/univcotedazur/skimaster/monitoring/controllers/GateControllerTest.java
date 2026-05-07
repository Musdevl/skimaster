package fr.univcotedazur.skimaster.monitoring.controllers;

import fr.univcotedazur.skimaster.monitoring.components.MonitoringFacade;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.DetailDTO;
import fr.univcotedazur.skimaster.monitoring.dto.gate.GateInformationDTO;
import fr.univcotedazur.skimaster.monitoring.dto.gate.ThresholdAlertDTO;
import fr.univcotedazur.skimaster.monitoring.dto.gate.ThresholdsDTO;
import fr.univcotedazur.skimaster.monitoring.entities.PanelSeverity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@ExtendWith(MockitoExtension.class)
class GateControllerTest {

    @Mock
    private MonitoringFacade monitoringFacade;

    private MonitoringController monitoringController;

    private static final String GATE_ID = "gate-1";

    @BeforeEach
    void setUp() {
        monitoringController = new MonitoringController(monitoringFacade);
    }

    @Test
    void setThresholdsTest() {
        ThresholdsDTO dto = new ThresholdsDTO(10, 20);
        ResponseEntity<?> response = monitoringController.setThresholds(GATE_ID, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void reportProblemTest() {
        DetailDTO body = new DetailDTO("Sensor failure");
        ResponseEntity<?> response = monitoringController.reportProblem(GATE_ID, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void openTest() {
        DetailDTO body = new DetailDTO("Manual open");
        ResponseEntity<?> response = monitoringController.open(GATE_ID, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void closeTest() {
        DetailDTO body = new DetailDTO("Manual close");
        ResponseEntity<?> response = monitoringController.close(GATE_ID, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void informGateOpeningTest() {
        GateInformationDTO dto = new GateInformationDTO("GateA", "Details");
        ResponseEntity<String> response = monitoringController.informGateOpening(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Gate Opened Successfully", response.getBody());
    }

    @Test
    void informGateClosingTest() {
        GateInformationDTO dto = new GateInformationDTO("GateB", "Details");
        ResponseEntity<String> response = monitoringController.informGateClosing(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Gate Closed Successfully", response.getBody());
    }

    @Test
    void thresholdAlertTest() {
        ThresholdAlertDTO alert = new ThresholdAlertDTO("gateId", 1, 1, PanelSeverity.INFO);
        ResponseEntity<?> response = monitoringController.thresholdAlert(alert);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}