package fr.univcotedazur.panel.controllers;

import fr.univcotedazur.panel.dto.PanelGateStatusDTO;
import fr.univcotedazur.panel.dto.PanelMessageDTO;
import fr.univcotedazur.panel.components.PanelService;
import fr.univcotedazur.panel.dto.PanelStatusDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/panel", produces = APPLICATION_JSON_VALUE)
public class PanelController {

    private final PanelService panelService;

    public PanelController(PanelService panelService) {
        this.panelService = panelService;
    }

    @GetMapping
    public ResponseEntity<PanelStatusDTO> read(){
        return ResponseEntity.ok(panelService.read());
    }

    @PutMapping(path="/message")
    public ResponseEntity<PanelStatusDTO> write(@RequestBody PanelMessageDTO messageDTO){ return ResponseEntity.ok(panelService.write(messageDTO)); }

    @DeleteMapping(path="/gate/{gateName}")
    public ResponseEntity<String> delete(@PathVariable String gateName){
        return ResponseEntity.ok(this.panelService.deleteGateStatus(gateName));
    }

    @PutMapping(path="/gate")
    public ResponseEntity<PanelGateStatusDTO> updateGateStatus(@RequestBody PanelGateStatusDTO panelGateStatusDTO){
        return ResponseEntity.ok(this.panelService.updateGateStatus(panelGateStatusDTO.gateName(), panelGateStatusDTO.status(), panelGateStatusDTO.detail()));
    }

    @PostMapping(path="/gate")
    public ResponseEntity<PanelGateStatusDTO> addGateStatus(@RequestBody PanelGateStatusDTO panelGateStatusDTO){
        return ResponseEntity.ok(this.panelService.addGateStatus(panelGateStatusDTO.gateName(), panelGateStatusDTO.status(), panelGateStatusDTO.detail()));
    }

    @PostMapping(path="/register")
    public ResponseEntity<String> register(){
        return ResponseEntity.ok(panelService.registerPanel());
    }

}