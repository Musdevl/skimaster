package fr.univcotedazur.skimaster.gate_cli.commands;

import fr.univcotedazur.skimaster.gate_cli.dto.DetailDTO;
import fr.univcotedazur.skimaster.gate_cli.utils.Format;
import org.springframework.http.MediaType;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.client.RestClient;

@ShellComponent
public class GateCommands {

    private final RestClient restClient;


    public GateCommands(RestClient restClient) {
        this.restClient = restClient;
    }

    @ShellMethod("Scan a user nfc card (scan NFC_CARD_ID)")
    public String scan(Long NfcCardId){
        return restClient.post()
                .uri("/scan")
                .body(NfcCardId)
                .retrieve()
                .body(String.class);
    }


    @ShellMethod("Report an issue (report-issue OPTIONAL_DETAIL)")
    public String reportIssue(@ShellOption(defaultValue = "") String details){
        return restClient.post()
                .uri("/report-issue")
                .body(new DetailDTO(details))
                .retrieve()
                .body(String.class);
    }

    @ShellMethod("Open Gate (gate-open OPTIONAL_DETAIL)")
    public String gateOpen(@ShellOption(defaultValue = "") String details){
        return restClient.post()
                .uri("/open")
                .body(new DetailDTO(details))
                .retrieve()
                .body(String.class);
    }

    @ShellMethod("close Gate (gate-close OPTIONAL_DETAIL)")
    public String gateClose(@ShellOption(defaultValue = "") String details){
        return restClient.post()
                .uri("/close")
                .body(new DetailDTO(details))
                .retrieve()
                .body(String.class);
    }

    @ShellMethod("Load mock (load-mock)")
    public String loadMock() {

        String mock = """
            [
              {
                "nfcId": 1,
                "customerId": 101,
                "sound": "HIGH_SOUND",
                "plan": "BASIC_PLAN"
              },
              {
                "nfcId": 2,
                "customerId": 102,
                "sound": "LOW_SOUND",
                "plan": "SUPER_CARD"
              },
              {
                "nfcId": 3,
                "customerId": 102,
                "sound": "LOW_SOUND",
                "plan": "FAMILY_PLAN"
              }
            ]
            """;

        return restClient.put()
                .uri("/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .body(mock)
                .retrieve()
                .body(String.class);
    }

    @ShellMethod("Register dhcp (dhcp DOMAIN_ID)")
    public String dhcp(Long domainId){
        return restClient.post()
                .uri("/gates/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(domainId)
                .retrieve()
                .body(String.class);
    }

    @ShellMethod("Get the current state of a gate (gate-status)")
    public String gateStatus(){
        return Format.prettyJson(get());
    }

    @ShellMethod("Generate daily report (daily-report OPTIONAL_DAY)")
    public String dailyReport(@org.springframework.shell.standard.ShellOption(defaultValue = "") String day) {
        String uri = (day == null || day.isBlank()) ? "/daily-report" : "/daily-report?day=" + day;
        return Format.prettyJson(post(uri, null));
    }

    private Object get() {
        return restClient.get()
                .uri("/status")
                .retrieve()
                .body(Object.class);
    }

    private Object post(String uri, Object body) {
        var request = restClient.post().uri(uri);
        if (body != null) {
            request.contentType(MediaType.APPLICATION_JSON).body(body);
        }
        return request.retrieve().body(Object.class);
    }
}
