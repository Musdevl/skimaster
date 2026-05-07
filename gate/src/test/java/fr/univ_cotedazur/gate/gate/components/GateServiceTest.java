package fr.univ_cotedazur.gate.gate.components;

import fr.univ_cotedazur.gate.gate.connector.SkimasterProxy;
import fr.univ_cotedazur.gate.gate.dto.AlertThresholdDTO;
import fr.univ_cotedazur.gate.gate.dto.GateStatusDTO;
import fr.univ_cotedazur.gate.gate.entities.NfcCard;
import fr.univ_cotedazur.gate.gate.entities.Plan;
import fr.univ_cotedazur.gate.gate.entities.Severity;
import fr.univ_cotedazur.gate.gate.entities.Sound;
import fr.univ_cotedazur.gate.gate.exceptions.CriticalThresholdException;
import fr.univ_cotedazur.gate.gate.exceptions.WarningThresholdException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GateServiceTest {

    @Mock private NfcCardRegistry nfcCardRegistry;
    @Mock private SkimasterProxy skimasterProxy;
    @Mock private SoundService soundService;

    @InjectMocks
    private GateService gateService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(gateService, "gateName", "MainGate");
        ReflectionTestUtils.setField(gateService, "gateUrl", "http://gate-1");
    }

    @Test
    void checkCard_GateClosed_ReturnsMessage() {
        String result = gateService.checkCard(123L);
        assertEquals("This gate is closed for the moment...", result);
        verifyNoInteractions(nfcCardRegistry);
    }

    @Test
    void checkCard_Success_PlaysSound() {
        gateService.openGate("Opening for test");
        NfcCard card = new NfcCard((long) 123, (long) 1, Sound.LOW_SOUND, Plan.BASIC_PLAN);
        
        when(nfcCardRegistry.findById((long) 123)).thenReturn(Optional.of(card));
        when(soundService.playValidSound(Sound.LOW_SOUND)).thenReturn("Sound: Low sound");

        String result = gateService.checkCard((long) 123);

        assertEquals("Sound: Low sound", result);
        verify(nfcCardRegistry).saveCardScan(card);
    }

    @Test
    void checkCard_CriticalThreshold_ClosesGateAndAlerts() {
        gateService.openGate("Start");
        NfcCard card = new NfcCard((long) 123, (long) 1, Sound.LOW_SOUND, Plan.BASIC_PLAN);
        
        when(nfcCardRegistry.findById((long) 123)).thenReturn(Optional.of(card));
        
        doThrow(new CriticalThresholdException("")).when(nfcCardRegistry).saveCardScan(card);

        gateService.checkCard(123L);

        GateStatusDTO status = gateService.getStatus();
        assertEquals("CRITICAL threshold reached ! Closing the gate...", status.detail());
        verify(skimasterProxy).sendThresholdAlert(eq("MainGate"), anyInt(), anyInt(), eq(Severity.CRITICAL));
        verify(skimasterProxy).informClosing(eq("MainGate"), anyString());
    }

    @Test
    void checkCard_WarningThreshold_SendsAlertOnly() {
        gateService.openGate("Start");
        NfcCard card = new NfcCard((long) 123, (long) 1, Sound.LOW_SOUND, Plan.BASIC_PLAN);
        
        when(nfcCardRegistry.findById((long) 123)).thenReturn(Optional.of(card));
        doThrow(new WarningThresholdException("")).when(nfcCardRegistry).saveCardScan(card);

        gateService.checkCard(123L);

        verify(skimasterProxy).sendThresholdAlert(eq("MainGate"), anyInt(), anyInt(), eq(Severity.WARNING));
    }

    @Test
    void openAndClose_CalculatesTime() throws InterruptedException {
        gateService.openGate("Morning");
        Thread.sleep(100); 
        
        gateService.closeGate("Night");
        
        assertTrue(gateService.getOpenedMinutes() >= 0);
        verify(skimasterProxy).informOpening(anyString(), anyString());
        verify(skimasterProxy).informClosing(anyString(), anyString());
    }

    @Test
    void setThresholds_ValidatesInput() {
        AlertThresholdDTO invalid = new AlertThresholdDTO(-1, 10);
        String result = gateService.setThresholds(invalid);
        
        assertEquals("Error: Threshold Values must be positives", result);
        verify(nfcCardRegistry, never()).setWarningThreshold(anyInt());

        AlertThresholdDTO valid = new AlertThresholdDTO(10, 20);
        gateService.setThresholds(valid);
        verify(nfcCardRegistry).setWarningThreshold(10);
        verify(nfcCardRegistry).setCriticalThreshold(20);
    }

    @Test
    void reportIssue_ClosesGateLocally() {
        gateService.openGate("Ok");
        gateService.reportIssue("Broken sensor");
        
        assertFalse(gateService.getStatus().detail().contains("successfully"));
        verify(skimasterProxy).reportIssue("MainGate", "Broken sensor");
    }
}