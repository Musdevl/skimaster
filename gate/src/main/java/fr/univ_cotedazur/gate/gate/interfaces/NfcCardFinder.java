package fr.univ_cotedazur.gate.gate.interfaces;

import fr.univ_cotedazur.gate.gate.dto.NFCCardDTO;
import fr.univ_cotedazur.gate.gate.entities.NfcCard;
import fr.univ_cotedazur.gate.gate.entities.Plan;

import java.util.List;
import java.util.Set;

public interface NfcCardFinder {
    List<NfcCard> findAllByPlan(Plan plan);

    List<NFCCardDTO> findTodaySuperCardScans();

    Set<Long> findNfcIds();
}
