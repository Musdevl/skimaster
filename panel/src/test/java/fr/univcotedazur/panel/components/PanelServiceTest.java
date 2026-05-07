package fr.univcotedazur.panel.components;

import fr.univcotedazur.panel.connector.interfaces.Skimaster;
import fr.univcotedazur.panel.dto.PanelMessageDTO;
import fr.univcotedazur.panel.dto.PanelStatusDTO;
import fr.univcotedazur.panel.entities.GateStatus;
import fr.univcotedazur.panel.entities.PanelGateStatus;
import fr.univcotedazur.panel.entities.PanelMessage;
import fr.univcotedazur.panel.entities.PanelSeverity;
import fr.univcotedazur.panel.exceptions.GateStatusAlreadyExistsException;
import fr.univcotedazur.panel.exceptions.NoGateFoundException;
import fr.univcotedazur.panel.repositories.PanelGateStatusRepository;
import fr.univcotedazur.panel.repositories.PanelMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PanelServiceTest {

    @Mock private PanelMessageRepository panelMessageRepository;
    @Mock private Skimaster skimasterProxy;
    @Mock private SimulatedClock clock;
    @Mock private PanelGateStatusRepository panelGateStatusRepository;

    @InjectMocks
    private PanelService panelService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(panelService, "panelName", "MainPanel");
        ReflectionTestUtils.setField(panelService, "panelURI", "http://panel-uri");
    }

    @Test
    void read_withLatestMessage_returnsMessage() {
        PanelMessage latest = new PanelMessage(Instant.now(), PanelSeverity.WARNING, "Watch out");
        when(panelMessageRepository.findTopByOrderByAtDesc()).thenReturn(Optional.of(latest));
        when(panelGateStatusRepository.findAll()).thenReturn(List.of());

        PanelStatusDTO result = panelService.read();

        assertEquals("Watch out", result.panelMessage().message());
    }

    @Test
    void read_withNoMessage_returnsDefaultInfo() {
        Instant now = Instant.now();
        when(clock.now()).thenReturn(now);
        when(panelMessageRepository.findTopByOrderByAtDesc()).thenReturn(Optional.empty());
        when(panelGateStatusRepository.findAll()).thenReturn(List.of());

        PanelStatusDTO result = panelService.read();

        assertEquals("Nothing for the moment...", result.panelMessage().message());
        assertEquals(PanelSeverity.INFO, result.panelMessage().severity());
    }

    // --- TESTS write() ---

    @Test
    void write_savesAndReturnsDto() {
        PanelMessageDTO dto = new PanelMessageDTO(Instant.now(), PanelSeverity.CRITICAL, "Emergency");
        when(panelGateStatusRepository.findAll()).thenReturn(List.of());
        when(panelMessageRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        PanelStatusDTO result = panelService.write(dto);

        assertEquals("Emergency", result.panelMessage().message());
        verify(panelMessageRepository).save(any(PanelMessage.class));
    }

    // --- TESTS Proxy & Utils ---

    @Test
    void registerPanel_callsProxy() {
        when(skimasterProxy.registerPanel("MainPanel", "http://panel-uri")).thenReturn("OK");
        String result = panelService.registerPanel();
        assertEquals("OK", result);
    }

    // --- TESTS Gate Status (CRUD) ---

    @Test
    void updateGateStatus_Success() {
        PanelGateStatus gate = new PanelGateStatus("Gate1", GateStatus.CLOSED, "Init");
        when(panelGateStatusRepository.findById("Gate1")).thenReturn(Optional.of(gate));
        when(panelGateStatusRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        panelService.updateGateStatus("Gate1", GateStatus.OPENED, "Manual override");

        assertEquals(GateStatus.OPENED, gate.getStatus());
        assertEquals("Manual override", gate.getDetail());
    }

    @Test
    void updateGateStatus_NotFound_ThrowsException() {
        when(panelGateStatusRepository.findById("Unknown")).thenReturn(Optional.empty());
        assertThrows(NoGateFoundException.class, () -> 
            panelService.updateGateStatus("Unknown", GateStatus.OPENED, "")
        );
    }

    @Test
    void addGateStatus_Success() {
        when(panelGateStatusRepository.findById("NewGate")).thenReturn(Optional.empty());
        when(panelGateStatusRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        panelService.addGateStatus("NewGate", GateStatus.OPENED, "First setup");

        verify(panelGateStatusRepository).save(any(PanelGateStatus.class));
    }

    @Test
    void addGateStatus_AlreadyExists_ThrowsException() {
        when(panelGateStatusRepository.findById("ExistingGate")).thenReturn(Optional.of(new PanelGateStatus()));
        
        assertThrows(GateStatusAlreadyExistsException.class, () -> 
            panelService.addGateStatus("ExistingGate", GateStatus.CLOSED, "")
        );
    }

    @Test
    void deleteGateStatus_callsRepository() {
        String result = panelService.deleteGateStatus("Gate1");
        verify(panelGateStatusRepository).deleteById("Gate1");
        assertEquals("Gate status successfully deleted", result);
    }
}