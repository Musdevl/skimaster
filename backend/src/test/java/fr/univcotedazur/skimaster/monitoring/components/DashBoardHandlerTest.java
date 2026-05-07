package fr.univcotedazur.skimaster.monitoring.components;

import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.GateStatusDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.externaldto.PanelStatusDTO;
import fr.univcotedazur.skimaster.monitoring.connectors.interfaces.Gate;
import fr.univcotedazur.skimaster.monitoring.connectors.interfaces.Panel;
import fr.univcotedazur.skimaster.monitoring.dto.DashboardDTO;
import fr.univcotedazur.skimaster.monitoring.entities.GateConnection;
import fr.univcotedazur.skimaster.monitoring.entities.GateStatus;
import fr.univcotedazur.skimaster.monitoring.entities.PanelConnection;
import fr.univcotedazur.skimaster.monitoring.repositories.GateConnectionRepository;
import fr.univcotedazur.skimaster.monitoring.repositories.PanelConnectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashBoardHandlerTest {

    @Mock private GateConnectionRepository gateRepo;
    @Mock private PanelConnectionRepository panelRepo;
    @Mock private Gate gateProxy;
    @Mock private Panel panelProxy;
    @Mock private SimulatedClock clock;

    private DashboardHandler dashboardHandler;

    @BeforeEach
    void setUp() {
        dashboardHandler = new DashboardHandler(gateRepo, panelProxy, clock, gateProxy, panelRepo);
    }

    @Test
    void getDashboard_Success_FullData() {
        GateConnection g1 = new GateConnection("g1", "http://gate1");
        PanelConnection p1 = new PanelConnection("p1", "http://panel1");
        
        when(gateRepo.findAll()).thenReturn(List.of(g1));
        when(panelRepo.findAll()).thenReturn(List.of(p1));

        GateStatusDTO gStatus = new GateStatusDTO("gate1", GateStatus.OPENED, 0.0, 0, 0, (long) 1, (long) 1, "", (long) 0, "");
        PanelStatusDTO pStatus = new PanelStatusDTO(null, null);

        when(gateProxy.requestStatus("http://gate1")).thenReturn(Optional.of(gStatus));
        when(panelProxy.read("http://panel1")).thenReturn(Optional.of(pStatus));

        DashboardDTO result = dashboardHandler.getDashboard();

        assertEquals(1, result.gates().size());
        assertEquals(1, result.panels().size());
    }

    @Test
    void getDashboard_WithEmptyProxies_FiltersEmptyOptionals() {
        when(clock.now()).thenReturn(Instant.now());
        
        GateConnection g1 = new GateConnection("g1", "http://gate1");
        PanelConnection p1 = new PanelConnection("p1", "http://panel1");

        when(gateRepo.findAll()).thenReturn(List.of(g1));
        when(panelRepo.findAll()).thenReturn(List.of(p1));

        when(gateProxy.requestStatus(anyString())).thenReturn(Optional.empty());
        when(panelProxy.read(anyString())).thenReturn(Optional.empty());

        DashboardDTO result = dashboardHandler.getDashboard();

        assertEquals(0, result.gates().size());
        assertEquals(0, result.panels().size());
    }

    @Test
    void getDashboard_NoConnectionsInRepo_ReturnsEmptyDashboard() {
        Instant now = Instant.now();
        when(clock.now()).thenReturn(now);
        when(gateRepo.findAll()).thenReturn(List.of());
        when(panelRepo.findAll()).thenReturn(List.of());

        DashboardDTO result = dashboardHandler.getDashboard();

        assertTrue(result.gates().isEmpty());
        assertTrue(result.panels().isEmpty());
        
        verifyNoInteractions(gateProxy);
        verifyNoInteractions(panelProxy);
    }
}