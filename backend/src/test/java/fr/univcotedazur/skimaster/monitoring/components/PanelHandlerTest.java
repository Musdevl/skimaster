package fr.univcotedazur.skimaster.monitoring.components;

import fr.univcotedazur.skimaster.monitoring.connectors.PanelProxy;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.PanelStatusDTO;
import fr.univcotedazur.skimaster.monitoring.dto.panel.PanelMessageDTO;
import fr.univcotedazur.skimaster.monitoring.entities.GateStatus;
import fr.univcotedazur.skimaster.monitoring.entities.PanelConnection;
import fr.univcotedazur.skimaster.monitoring.entities.PanelSeverity;
import fr.univcotedazur.skimaster.monitoring.exceptions.PanelNotFoundException;
import fr.univcotedazur.skimaster.monitoring.repositories.PanelConnectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PanelHandlerTest {

    @Mock PanelProxy panelProxy;
    @Mock PanelConnectionRepository panelConnectionRepository;
    @Mock SimulatedClock clock;

    @InjectMocks PanelHandler panelHandler;

    private PanelConnection panel;
    private PanelStatusDTO someStatus;

    @BeforeEach
    void setUp() {
        panel = mock(PanelConnection.class);
        someStatus = new PanelStatusDTO(new ArrayList<>(), new PanelMessageDTO(Instant.now(), PanelSeverity.INFO, ""));
    }

    // read

    @Test
    void read_panelNotFound_throwsPanelNotFoundException() {
        when(panelConnectionRepository.findById("p1")).thenReturn(Optional.empty());
        assertThrows(PanelNotFoundException.class, () -> panelHandler.read("p1"));
    }

    @Test
    void read_proxyReturnsStatus_returnsIt() {
        when(panel.getURI()).thenReturn("http://panel:8080");
        when(panelConnectionRepository.findById("p1")).thenReturn(Optional.of(panel));
        when(panelProxy.read("http://panel:8080")).thenReturn(Optional.of(someStatus));

        assertEquals(someStatus, panelHandler.read("p1"));
    }

    @Test
    void read_proxyReturnsEmpty_returnsFallback() {
        when(panel.getURI()).thenReturn("http://panel:8080");
        when(panelConnectionRepository.findById("p1")).thenReturn(Optional.of(panel));
        when(panelProxy.read("http://panel:8080")).thenReturn(Optional.empty());

        PanelStatusDTO result = panelHandler.read("p1");
        assertNotNull(result);
        assertEquals("No message for the moment...", result.panelMessage().message());
    }

    // write(panelId, message, severity)

    @Test
    void writeById_panelNotFound_throwsPanelNotFoundException() {
        when(panelConnectionRepository.findById("p1")).thenReturn(Optional.empty());
        assertThrows(PanelNotFoundException.class, () -> panelHandler.write("p1", "hello", PanelSeverity.INFO));
    }

    @Test
    void writeById_proxyReturnsStatus_returnsIt() {
        when(panel.getURI()).thenReturn("http://panel:8080");
        when(panelConnectionRepository.findById("p1")).thenReturn(Optional.of(panel));
        when(panelProxy.write(eq("http://panel:8080"), any(PanelMessageDTO.class))).thenReturn(Optional.of(someStatus));

        assertEquals(someStatus, panelHandler.write("p1", "hello", PanelSeverity.INFO));
    }

    @Test
    void writeById_proxyReturnsEmpty_returnsFallback() {
        when(panel.getURI()).thenReturn("http://panel:8080");
        when(panelConnectionRepository.findById("p1")).thenReturn(Optional.of(panel));
        when(panelProxy.write(eq("http://panel:8080"), any(PanelMessageDTO.class))).thenReturn(Optional.empty());

        PanelStatusDTO result = panelHandler.write("p1", "hello", PanelSeverity.INFO);
        assertEquals("No message for the moment...", result.panelMessage().message());
    }

    // write(message, severity)

    @Test
    void writeBroadcast_noPanels_doesNothing() {
        when(panelConnectionRepository.findAll()).thenReturn(List.of());
        panelHandler.write("alert", PanelSeverity.WARNING);
        verifyNoInteractions(panelProxy);
    }

    @Test
    void writeBroadcast_twoPanels_writesToBoth() {
        when(panel.getURI()).thenReturn("http://panel:8080");
        PanelConnection panel2 = mock(PanelConnection.class);
        when(panel2.getURI()).thenReturn("http://panel2:8080");
        when(panelConnectionRepository.findAll()).thenReturn(List.of(panel, panel2));

        panelHandler.write("alert", PanelSeverity.WARNING);

        verify(panelProxy).write(eq("http://panel:8080"), any(PanelMessageDTO.class));
        verify(panelProxy).write(eq("http://panel2:8080"), any(PanelMessageDTO.class));
    }

    // updateGateStatus

    @Test
    void updateGateStatus_noPanels_doesNothing() {
        when(panelConnectionRepository.findAll()).thenReturn(List.of());
        panelHandler.updateGateStatus("gate1", GateStatus.OPENED, "ok");
        verifyNoInteractions(panelProxy);
    }

    @Test
    void updateGateStatus_twoPanels_updatesAll() {
        when(panel.getURI()).thenReturn("http://panel:8080");
        PanelConnection panel2 = mock(PanelConnection.class);
        when(panel2.getURI()).thenReturn("http://panel2:8080");
        when(panelConnectionRepository.findAll()).thenReturn(List.of(panel, panel2));

        panelHandler.updateGateStatus("gate1", GateStatus.OPENED, "ok");

        verify(panelProxy).updateGateStatus("http://panel:8080", "gate1", GateStatus.OPENED, "ok");
        verify(panelProxy).updateGateStatus("http://panel2:8080", "gate1", GateStatus.OPENED, "ok");
    }

    // addGateStatus

    @Test
    void addGateStatus_panelNotFound_throwsPanelNotFoundException() {
        when(panelConnectionRepository.findById("p1")).thenReturn(Optional.empty());
        assertThrows(PanelNotFoundException.class, () -> panelHandler.addGateStatus("p1", "gate1", GateStatus.OPENED, "ok"));
    }

    @Test
    void addGateStatus_proxySucceeds_returnsMessage() {
        when(panel.getURI()).thenReturn("http://panel:8080");
        when(panelConnectionRepository.findById("p1")).thenReturn(Optional.of(panel));
        when(panelProxy.addGateStatus("http://panel:8080", "gate1", GateStatus.OPENED, "ok"))
                .thenReturn(Optional.of("saved"));

        assertEquals("saved", panelHandler.addGateStatus("p1", "gate1", GateStatus.OPENED, "ok"));
    }

    @Test
    void addGateStatus_proxyFails_returnsFallback() {
        when(panel.getURI()).thenReturn("http://panel:8080");
        when(panelConnectionRepository.findById("p1")).thenReturn(Optional.of(panel));
        when(panelProxy.addGateStatus("http://panel:8080", "gate1", GateStatus.OPENED, "ok"))
                .thenReturn(Optional.empty());

        assertEquals("Failed to save gate status", panelHandler.addGateStatus("p1", "gate1", GateStatus.OPENED, "ok"));
    }

    // removeGateStatus

    @Test
    void removeGateStatus_panelNotFound_throwsPanelNotFoundException() {
        when(panelConnectionRepository.findById("p1")).thenReturn(Optional.empty());
        assertThrows(PanelNotFoundException.class, () -> panelHandler.removeGateStatus("p1", "gate1"));
    }

    @Test
    void removeGateStatus_proxySucceeds_returnsMessage() {
        when(panel.getURI()).thenReturn("http://panel:8080");
        when(panelConnectionRepository.findById("p1")).thenReturn(Optional.of(panel));
        when(panelProxy.removeGateStatus("http://panel:8080", "gate1")).thenReturn(Optional.of("removed"));

        assertEquals("removed", panelHandler.removeGateStatus("p1", "gate1"));
    }

    @Test
    void removeGateStatus_proxyFails_returnsFallback() {
        when(panel.getURI()).thenReturn("http://panel:8080");
        when(panelConnectionRepository.findById("p1")).thenReturn(Optional.of(panel));
        when(panelProxy.removeGateStatus("http://panel:8080", "gate1")).thenReturn(Optional.empty());

        assertEquals("Failed to remove gate status", panelHandler.removeGateStatus("p1", "gate1"));
    }

    // registerPanel

    @Test
    void registerPanel_savesNewConnection() {
        panelHandler.registerPanel("p1", "http://panel:8080");
        verify(panelConnectionRepository).save(any(PanelConnection.class));
    }
}