package fr.univcotedazur.skimaster.nfc.controllers;

import fr.univcotedazur.skimaster.nfc.components.NFCCardRegistry;
import fr.univcotedazur.skimaster.nfc.dto.NFCCardDTO;
import fr.univcotedazur.skimaster.nfc.interfaces.NFCCardFinder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class NFCCardController {

    public static final String NFC_CARD_URI = "/nfc-cards";

    private final NFCCardFinder finder;
    private final NFCCardRegistry nfcCardRegistry;

    public NFCCardController(NFCCardFinder finder, NFCCardRegistry nfcCardRegistry) {
        this.finder = finder;
        this.nfcCardRegistry = nfcCardRegistry;
    }

    @GetMapping(path = NFC_CARD_URI, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NFCCardDTO>> getAllNFCCard() {
        return ResponseEntity.ok(finder.findAll().stream().map(nfcCardRegistry::convertNFCCardtoNFCCardDTO).toList());
    }
}
