package fr.univ_cotedazur.gate.gate.components;

import fr.univ_cotedazur.gate.gate.connector.SkimasterProxy;
import fr.univ_cotedazur.gate.gate.dto.AlertThresholdDTO;
import fr.univ_cotedazur.gate.gate.dto.GateDailyReportDTO;
import fr.univ_cotedazur.gate.gate.dto.GateStatusDTO;
import fr.univ_cotedazur.gate.gate.entities.GateStatus;
import fr.univ_cotedazur.gate.gate.entities.NfcCard;
import fr.univ_cotedazur.gate.gate.entities.Plan;
import fr.univ_cotedazur.gate.gate.entities.Severity;
import fr.univ_cotedazur.gate.gate.exceptions.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class GateService {

    private final NfcCardRegistry nfcCardRegistry;

    private final SkimasterProxy skimasterProxy;

    private final SoundService soundService;

    private boolean isOpened;

    private Instant lastOpening;
    private Instant lastClosing;

    private double totalOpenSeconds;

    @Value("${gate.name}")
    private String gateName;

    @Value("${gate.url}")
    private String gateUrl;

    private Long domainId;

    private String detail;


    public GateService(NfcCardRegistry nfcCardRegistry, SkimasterProxy skimasterProxy, SoundService soundService) {
        this.nfcCardRegistry = nfcCardRegistry;
        this.skimasterProxy = skimasterProxy;
        this.soundService = soundService;
        this.isOpened = false;
        this.domainId = null;
        this.detail = "Nothing special for the moment...";
    }

    public void setWarningThreshold(int newWarningThreshold) {
        this.nfcCardRegistry.setWarningThreshold(newWarningThreshold);
    }

    public void setCriticalThreshold(int newCriticalThreshold) {
        this.nfcCardRegistry.setCriticalThreshold(newCriticalThreshold);
    }

    public String checkCard(Long nfcId){
        if(isOpened){
            Optional<NfcCard> nfcCard = nfcCardRegistry.findById(nfcId);
            if(nfcCard.isPresent()){
                String res = soundService.playValidSound(nfcCard.get().getSound());

               try{
                    nfcCardRegistry.saveCardScan(nfcCard.get());
                   this.detail = "Nothing special for the moment...";
               } catch (CriticalThresholdException e){
                    this.closeGate("Critical threshold reached");
                   this.detail = Severity.CRITICAL.name() + " threshold reached ! Closing the gate...";
                    skimasterProxy.sendThresholdAlert(gateName, (int) nfcCardRegistry.getLastMinutePassage(), nfcCardRegistry.getCriticalThreshold(), Severity.CRITICAL);
                }catch (WarningThresholdException e){
                   this.detail = Severity.WARNING.name() + " threshold reached !";
                   skimasterProxy.sendThresholdAlert(gateName, (int) nfcCardRegistry.getLastMinutePassage(), nfcCardRegistry.getWarningThreshold(), Severity.WARNING);
                }
                return res;
            }
            else{
                return soundService.playInvalidSound();
            }
        } else {
            return "This gate is closed for the moment...";
        }
    }


    public String closeGate(String details) {
        if (isOpened) {
            this.skimasterProxy.informClosing(gateName, details);
            this.isOpened = false;
            totalOpenSeconds += Instant.now().getEpochSecond() - lastOpening.getEpochSecond();
            this.lastOpening = null;
            return "Gate Closed successfully";
        }
        return "Gate is Already closed";
    }

    public String openGate(String details) {
        this.skimasterProxy.informOpening(gateName, details);
        this.isOpened = true;
        this.lastOpening = Instant.now();
        this.detail = "Gate opened successfully";
        return "Gate Opened successfully";
    }


    public List<NfcCard> getDailySuperCard() {
        return this.nfcCardRegistry.findAllByPlan(Plan.SUPER_CARD);
    }

    public void reportIssue(String details){
        this.isOpened = false;
        this.detail = "Issue reported, closing the gate...";
        this.skimasterProxy.reportIssue(this.gateName, details);
    }

    public void registerGate(Long domainId){
        this.skimasterProxy.registerGate(gateName, gateUrl, domainId);
        this.domainId = domainId;
    }

    public String setThresholds(AlertThresholdDTO thresholds){
        if(thresholds.warning_threshold() < 0 || thresholds.critical_threshold() < 0){
            return "Error: Threshold Values must be positives";
        }
        this.setCriticalThreshold(thresholds.critical_threshold());
        this.setWarningThreshold(thresholds.warning_threshold());
        return "Thresholds updated successfully";
    }

    public GateStatusDTO getStatus(){
        GateStatus current_status = GateStatus.CLOSED;

        if(this.isOpened){ current_status = GateStatus.OPENED; }

        return new GateStatusDTO(
                gateName,
                current_status,
                this.getOpenedMinutes(),
                this.nfcCardRegistry.getWarningThreshold(),
                this.nfcCardRegistry.getCriticalThreshold(),
                this.nfcCardRegistry.getDailyPassageCount(),
                this.nfcCardRegistry.getLastMinutePassage(),
                this.detail,
                this.domainId,
                this.gateUrl
        );
    }

    public double getOpenedMinutes(){
        double totalSeconds = totalOpenSeconds;
        if (isOpened) {
            totalSeconds += Instant.now().getEpochSecond() - lastOpening.getEpochSecond();
        }
        return totalSeconds / 60;
    }

    public GateDailyReportDTO getDailyReport() {
        double totalSeconds = totalOpenSeconds;
        if (isOpened) {
            totalSeconds += Instant.now().getEpochSecond() - lastOpening.getEpochSecond();
        }
        return new GateDailyReportDTO(
                gateName,
                (int) this.nfcCardRegistry.getDailyPassageCount(),
                this.getOpenedMinutes(),
                this.nfcCardRegistry.findNfcIds()
        );
    }
}
