package fr.univcotedazur.skimaster.cli.commands;

import fr.univcotedazur.skimaster.cli.dto.CliPanelMessageDTO;
import fr.univcotedazur.skimaster.cli.model.PanelSeverity;
import fr.univcotedazur.skimaster.cli.dto.PanelStatusDTO;
import fr.univcotedazur.skimaster.cli.utils.Format;
import org.springframework.http.MediaType;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@ShellComponent
public class MonitoringCommands {
    private final RestClient restClient;

    public MonitoringCommands(RestClient restClient) {
        this.restClient = restClient;
    }


    @ShellMethod("Configure thresholds for a gate (set-gate-threshold GATE_ID WARNING_THRESHOLD CRITICAL_THRESHOLD)" )
    public String setGateThreshold(String gateId,
                                   @ShellOption(defaultValue = "0") int warning,
                                   @ShellOption(defaultValue = "0") int critical) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("warning", warning);
        body.put("critical", critical);

        restClient.put()
                .uri("/monitoring/gates/" + gateId + "/thresholds")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();

        return "OK";
    }

    @ShellMethod("Reopen a gate (gate-open GATE_ID OPTIONAL_DETAILS)")
    public String gateOpen(String gateId, @ShellOption(defaultValue = "") String details) {
        Map<String, Object> body = Map.of("details", details);
        restClient.post()
                .uri("/monitoring/gates/"+ gateId + "/open")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
        return "OK";
    }

    @ShellMethod("Close a gate (gate-close GATE_ID OPTIONAL_DETAILS)")
    public String gateClose(String gateId, @ShellOption(defaultValue = "") String details) {
        Map<String, Object> body = Map.of("details", details);
        restClient.post()
                .uri("/monitoring/gates/" + gateId + "/close")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
        return "OK";
    }

    @ShellMethod("Write a message on a panel (write-panel PANEL_NAME MESSAGE SEVERITY)")
    public PanelStatusDTO writePanel(String panelName, String message, String severity){
        return restClient.put()
                .uri("/monitoring/panels/" + panelName)
                .body(new CliPanelMessageDTO(message, PanelSeverity.valueOf(severity)))
                .retrieve()
                .body(PanelStatusDTO.class);
    }

    @ShellMethod("Read a message on a panel (read-panel PANEL_NAME)")
    public PanelStatusDTO readPanel(String panelName){
        return restClient.get()
                .uri("/monitoring/panels/" + panelName)
                .retrieve()
                .body(PanelStatusDTO.class);
    }

    @ShellMethod("Add a gate status on a panel (add-panel-gate PANEL_NAME GATE_NAME")
    public String addPanelGate(String panelName, String gateName){
        return restClient.post()
                .uri("/monitoring/panels/"+panelName)
                .body(gateName)
                .retrieve()
                .body(String.class);
    }

    @ShellMethod("Add a gate status on a panel (remove-panel-gate PANEL_NAME GATE_NAME")
    public String removePanelGate(String panelName, String gateName){
        return restClient.delete()
                .uri("/monitoring/panels/"+panelName + "/gate/" + gateName)
                .retrieve()
                .body(String.class);
    }
}
