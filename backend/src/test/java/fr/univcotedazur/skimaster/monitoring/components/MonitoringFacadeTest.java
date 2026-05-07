package fr.univcotedazur.skimaster.monitoring.components;

import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateStatusDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.PanelStatusDTO;
import fr.univcotedazur.skimaster.monitoring.dto.gate.ThresholdAlertDTO;
import fr.univcotedazur.skimaster.monitoring.dto.gate.ThresholdsDTO;
import fr.univcotedazur.skimaster.monitoring.entities.GateStatus;
import fr.univcotedazur.skimaster.monitoring.entities.PanelSeverity;
import fr.univcotedazur.skimaster.monitoring.entities.GateConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonitoringFacadeTest {

    @Mock
    private GateHandler gateHandler;

    @Mock
    private PanelHandler panelHandler;

    @Mock
    private GateConnection mockConnection;

    private MonitoringFacade monitoringFacade;

    private static final String GATE_ID = "gate-1";
    private static final String PANEL_ID = "panel-1";

    @BeforeEach
    void setUp() {
        monitoringFacade = new MonitoringFacade(gateHandler, panelHandler);
    }

    @Test
    void configureThresholds_delegates() {
        ThresholdsDTO dto = new ThresholdsDTO(10, 20);
        monitoringFacade.configureThresholds(GATE_ID, dto);
        verify(gateHandler).configureThresholds(GATE_ID, dto);
    }

    @Test
    void reportProblem_withDetails() {
        when(gateHandler.getGateConnection(GATE_ID)).thenReturn(mockConnection);
        monitoringFacade.reportProblem(GATE_ID, "Broken");
        
        verify(panelHandler).updateGateStatus(eq(GATE_ID), eq(GateStatus.CLOSED), contains("Broken"));
    }

    @Test
    void reportProblem_withNullDetails_usesSafe() {
        when(gateHandler.getGateConnection(GATE_ID)).thenReturn(mockConnection);
        monitoringFacade.reportProblem(GATE_ID, null);
        
        verify(panelHandler).updateGateStatus(eq(GATE_ID), eq(GateStatus.CLOSED), contains("(no details)"));
    }

    @Test
    void open_delegatesWithSafeDetails() {
        when(gateHandler.getGateConnection(GATE_ID)).thenReturn(mockConnection);
        monitoringFacade.open(GATE_ID, " "); // isBlank test
        
        verify(panelHandler).updateGateStatus(eq(GATE_ID), eq(GateStatus.OPENED), contains("(no details)"));
    }

    @Test
    void close_delegatesWithDetails() {
        when(gateHandler.getGateConnection(GATE_ID)).thenReturn(mockConnection);
        monitoringFacade.close(GATE_ID, "Manual close");
        
        verify(panelHandler).updateGateStatus(eq(GATE_ID), eq(GateStatus.CLOSED), contains("Manual close"));
    }

    @Test
    void openManual_and_closeManual_delegateDirectly() {
        monitoringFacade.openManual(GATE_ID, "details");
        monitoringFacade.closeManual(GATE_ID, "details");
        
        verify(gateHandler).openManual(GATE_ID, "details");
        verify(gateHandler).closeManual(GATE_ID, "details");
    }

    @Test
    void handleThresholdAlert_formatsCorrectly() {
        ThresholdAlertDTO alert = new ThresholdAlertDTO(GATE_ID, 50, 40, PanelSeverity.WARNING);
        monitoringFacade.handleThresholdAlert(alert);
        
        verify(panelHandler).write(argThat(msg -> msg.contains(GATE_ID) && msg.contains("severity=WARNING")), eq(PanelSeverity.WARNING));
    }

    @Test
    void registerPanel_delegates() {
        monitoringFacade.registerPanel(PANEL_ID, "http://uri");
        verify(panelHandler).registerPanel(PANEL_ID, "http://uri");
    }

    @Test
    void addPanelGateStatus_chainingCorrectly() {
        GateStatusDTO status = new GateStatusDTO(GATE_ID, GateStatus.OPENED, 0.0, 0, 0, (long) 1, (long) 1, "detail", (long) 0, "");
        when(gateHandler.getGateConnection(GATE_ID)).thenReturn(mockConnection);
        when(gateHandler.getGateStatus(mockConnection)).thenReturn(status);
        when(panelHandler.addGateStatus(anyString(), anyString(), any(), anyString())).thenReturn("Success");

        String result = monitoringFacade.addPanelGateStatus(PANEL_ID, GATE_ID);

        assertEquals("Success", result);
        verify(panelHandler).addGateStatus(PANEL_ID, GATE_ID, GateStatus.OPENED, "detail");
    }

    @Test
    void removePanelGateStatus_delegates() {
        when(panelHandler.removeGateStatus(PANEL_ID, GATE_ID)).thenReturn("Removed");
        String result = monitoringFacade.removePanelGateStatus(PANEL_ID, GATE_ID);
        assertEquals("Removed", result);
    }

    @Test
    void readAndWritePanel_delegate() {
        PanelStatusDTO mockStatus = mock(PanelStatusDTO.class);
        when(panelHandler.read(PANEL_ID)).thenReturn(mockStatus);
        when(panelHandler.write(PANEL_ID, "msg", PanelSeverity.INFO)).thenReturn(mockStatus);

        assertEquals(mockStatus, monitoringFacade.readPanel(PANEL_ID));
        assertEquals(mockStatus, monitoringFacade.writePanel(PANEL_ID, "msg", PanelSeverity.INFO));
    }
}