package fr.univcotedazur.skimaster.monitoring.components;

import fr.univcotedazur.skimaster.monitoring.connectors.GateProxy;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateStatusDTO;
import fr.univcotedazur.skimaster.monitoring.dto.gate.ThresholdsDTO;
import fr.univcotedazur.skimaster.monitoring.entities.GateConnection;
import fr.univcotedazur.skimaster.monitoring.entities.GateStatus;
import fr.univcotedazur.skimaster.monitoring.exceptions.FailedToGetGateStatusException;
import fr.univcotedazur.skimaster.monitoring.exceptions.GateNotFoundException;
import fr.univcotedazur.skimaster.monitoring.exceptions.InvalidThresholdsException;
import fr.univcotedazur.skimaster.monitoring.repositories.GateConnectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GateHandlerTest {

    @Mock
    private GateConnectionRepository repository;

    @Mock
    private GateProxy gateProxy;

    private GateHandler gateHandler;

    private static final String GATE_ID = "gate-1";
    private static final String GATE_URI = "http://gate-uri";

    @BeforeEach
    void setUp() {
        gateHandler = new GateHandler(repository, gateProxy);
    }

    // configureThresholds

    @Test
    void configureThresholds_Success() {
        GateConnection gate = new GateConnection(GATE_ID, GATE_URI);
        ThresholdsDTO dto = new ThresholdsDTO(10, 20);
        when(repository.findById(GATE_ID)).thenReturn(Optional.of(gate));

        gateHandler.configureThresholds(GATE_ID, dto);

        verify(gateProxy).setAlertThresholds(GATE_URI, 10, 20);
    }

    @Test
    void configureThresholds_NegativeValue_ThrowsException() {
        ThresholdsDTO dto = new ThresholdsDTO(-1, 10);
        assertThrows(InvalidThresholdsException.class, () -> gateHandler.configureThresholds(GATE_ID, dto));
    }

    @Test
    void configureThresholds_WarningHigherThanCritical_ThrowsException() {
        ThresholdsDTO dto = new ThresholdsDTO(30, 20);
        assertThrows(InvalidThresholdsException.class, () -> gateHandler.configureThresholds(GATE_ID, dto));
    }

    @Test
    void configureThresholds_GateNotFound_ThrowsException() {
        ThresholdsDTO dto = new ThresholdsDTO(10, 20);
        when(repository.findById(GATE_ID)).thenReturn(Optional.empty());

        assertThrows(GateNotFoundException.class, () -> gateHandler.configureThresholds(GATE_ID, dto));
    }

    // getGateStatus

    @Test
    void getGateStatus_Success() {
        GateConnection gate = new GateConnection(GATE_ID, GATE_URI);
        GateStatusDTO expectedStatus = new GateStatusDTO(GATE_ID, GateStatus.OPENED, 0.0, 0, 0, (long) 1, (long) 1, "", (long) 0, "");
        when(gateProxy.requestStatus(GATE_URI)).thenReturn(Optional.of(expectedStatus));

        GateStatusDTO result = gateHandler.getGateStatus(gate);

        assertEquals(expectedStatus, result);
    }

    @Test
    void getGateStatus_ProxyReturnsEmpty_ThrowsException() {
        GateConnection gate = new GateConnection(GATE_ID, GATE_URI);
        when(gateProxy.requestStatus(GATE_URI)).thenReturn(Optional.empty());

        assertThrows(FailedToGetGateStatusException.class, () -> gateHandler.getGateStatus(gate));
    }

    // getGateConnection

    @Test
    void getGateConnection_Success() {
        GateConnection gate = new GateConnection(GATE_ID, GATE_URI);
        when(repository.findById(GATE_ID)).thenReturn(Optional.of(gate));

        GateConnection result = gateHandler.getGateConnection(GATE_ID);
        assertEquals(gate, result);
    }

    // openManual & closeManual

    @Test
    void openManual_Success() {
        GateConnection gate = new GateConnection(GATE_ID, GATE_URI);
        when(repository.findById(GATE_ID)).thenReturn(Optional.of(gate));

        gateHandler.openManual(GATE_ID, "Force open");

        verify(gateProxy).openGate(GATE_URI, "Force open");
    }

    @Test
    void closeManual_Success() {
        GateConnection gate = new GateConnection(GATE_ID, GATE_URI);
        when(repository.findById(GATE_ID)).thenReturn(Optional.of(gate));

        gateHandler.closeManual(GATE_ID, "Emergency close");

        verify(gateProxy).closeGate(GATE_URI, "Emergency close");
    }
}