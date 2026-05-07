package fr.univcotedazur.skimaster.cli.commands;

import org.springframework.http.MediaType;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.client.RestClient;

import java.util.Map;

@ShellComponent
public class DemoCommands {

    public static final String BASE_URI = "http://";

    private final RestClient.Builder restClientBuilder;

    public DemoCommands(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    @ShellMethod("Scan a user nfc card (scan GATE_ID NFC_CARD_ID)")
    public String scanCard(String gateId, Long NfcCardId){
        RestClient restClient = restClientBuilder
                .baseUrl(BASE_URI + gateId + ":8090")
                .build();

        return restClient.post()
                .uri("/scan")
                .body(NfcCardId)
                .retrieve()
                .body(String.class);
    }

    @ShellMethod("Register a gate in a domain (register-gate GATE_NAME DOMAIN_ID)")
    public String registerGate(String gateId, String domainId){
        RestClient restClient = restClientBuilder
                .baseUrl(BASE_URI + gateId + ":8090")
                .build();

        return restClient.post()
                .uri("/gates/register")
                .body(Long.parseLong(domainId))
                .retrieve()
                .body(String.class);
    }

    @ShellMethod("Register a panel in the resort (register-panel PANEL_NAME)")
    public String registerPanel(String panelId){
        RestClient restClient = restClientBuilder
                .baseUrl(BASE_URI + panelId + ":8050")
                .build();
        return restClient.post()
                .uri("/panel/register")
                .retrieve()
                .body(String.class);
    }

    @ShellMethod("Report a problem on a gate, which closes it automatically and updates the panel (gate-issue GATE_ID OPTIONAL_DETAILS)")
    public String gateIssue(String gateId, @ShellOption(defaultValue = "") String details) {
        Map<String, Object> body = Map.of("details", details);

        RestClient restClient = restClientBuilder
                .baseUrl(BASE_URI + gateId + ":8090")
                .build();

        restClient.post()
                .uri("/report-issue")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
        return "OK";
    }
}
