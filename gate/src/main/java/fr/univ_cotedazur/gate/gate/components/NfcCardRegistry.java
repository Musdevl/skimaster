package fr.univ_cotedazur.gate.gate.components;

import fr.univ_cotedazur.gate.gate.entities.NfcCard;
import fr.univ_cotedazur.gate.gate.entities.NfcCardScan;
import fr.univ_cotedazur.gate.gate.entities.Plan;
import fr.univ_cotedazur.gate.gate.exceptions.CriticalThresholdException;
import fr.univ_cotedazur.gate.gate.exceptions.WarningThresholdException;
import fr.univ_cotedazur.gate.gate.interfaces.NfcCardFinder;
import fr.univ_cotedazur.gate.gate.interfaces.NfcCardRegistration;
import fr.univ_cotedazur.gate.gate.interfaces.NfcCardScanRegistration;
import fr.univ_cotedazur.gate.gate.repositories.NfcCardRepository;
import fr.univ_cotedazur.gate.gate.repositories.NfcCardScanRepository;
import fr.univ_cotedazur.gate.gate.dto.NFCCardDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
@Service
public class NfcCardRegistry implements NfcCardFinder, NfcCardScanRegistration, NfcCardRegistration {

    private final NfcCardRepository nfcCardRepository;
    private final NfcCardScanRepository nfcCardScanRepository;

    private int warning_threshold = 0;
    private int critical_threshold = 0;

    public NfcCardRegistry(NfcCardRepository nfcCardRepository, NfcCardScanRepository nfcCardScanRepository) {
        this.nfcCardRepository = nfcCardRepository;
        this.nfcCardScanRepository = nfcCardScanRepository;
    }

    public Optional<NfcCard> findById(Long id){
        return this.nfcCardRepository.findById(id);
    }

    @Override
    public List<NfcCard> findAllByPlan(Plan plan) {
        return this.nfcCardRepository.findAllByPlan(plan);
    }

    @Override
    public List<NFCCardDTO> findTodaySuperCardScans() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Long> scannedIds = nfcCardScanRepository.findScannedNfcIdsByPlan(start, end, Plan.SUPER_CARD);
        List<NFCCardDTO> result = new ArrayList<>();
        for (Long nfcId : scannedIds) {
            Optional<NfcCard> card = nfcCardRepository.findByNfcId(nfcId);
            card.ifPresent(nfcCard -> result.add(nfcCardToDto(nfcCard)));
        }
        return result;
    }

    public void deleteAll() { this.nfcCardRepository.deleteAll(); }

    public List<NfcCard> saveAll(List<NfcCard> cards) { return this.nfcCardRepository.saveAll(cards); }

    @Override
    public NfcCard save(NfcCard card) {
        return this.nfcCardRepository.save(card);
    }

    @Override
    public NfcCardScan saveCardScan(NfcCard card) {
        NfcCardScan scan = nfcCardScanRepository.save(new NfcCardScan(card.getNfcId()));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMinuteAgo = now.minusMinutes(1);
        long scansLastMinute = nfcCardScanRepository.countScansInInterval(oneMinuteAgo, now);

        if (scansLastMinute >= this.critical_threshold && this.critical_threshold > 0) {
            throw new CriticalThresholdException("Critical threshold reached");
        }

        if (scansLastMinute >= this.warning_threshold && this.warning_threshold > 0) {
            throw new WarningThresholdException("Warning threshold reached");
        }

        return scan;
    }

    public void setCriticalThreshold(int newCriticalThreshold) {
        this.critical_threshold = newCriticalThreshold;
    }

    public void setWarningThreshold(int newWarningThreshold){
        this.warning_threshold = newWarningThreshold;
    }

    public int getWarningThreshold() { return this.warning_threshold; }

    public int getCriticalThreshold() { return this.critical_threshold; }

    public long getLastMinutePassage() {
        LocalDateTime now = LocalDateTime.now();
        return nfcCardScanRepository.countScansInInterval(now.minusMinutes(1), now);
    }

    public long getDailyPassageCount() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return nfcCardScanRepository.countScansInInterval(start, end);
    }

    public Set<Long> findNfcIds(){
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return new HashSet<>(this.nfcCardScanRepository.findScannedNfcIds(start, end));
    }

    private NFCCardDTO nfcCardToDto(NfcCard nfcCard){
        return new NFCCardDTO(nfcCard.getNfcId(), nfcCard.getCustomerId(), nfcCard.getSound(), nfcCard.getPlan());
    }
}
