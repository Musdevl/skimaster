package fr.univ_cotedazur.gate.gate.interfaces;

import fr.univ_cotedazur.gate.gate.entities.NfcCard;
import fr.univ_cotedazur.gate.gate.entities.NfcCardScan;

public interface NfcCardScanRegistration {
    NfcCardScan saveCardScan(NfcCard card);
}
