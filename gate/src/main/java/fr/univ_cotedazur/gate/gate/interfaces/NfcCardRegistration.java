package fr.univ_cotedazur.gate.gate.interfaces;

import fr.univ_cotedazur.gate.gate.entities.NfcCard;

public interface NfcCardRegistration {
    NfcCard save(NfcCard card);
}
